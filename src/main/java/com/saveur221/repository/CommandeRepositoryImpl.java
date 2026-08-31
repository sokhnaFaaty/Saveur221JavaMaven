package com.saveur221.repository;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.saveur221.config.DatabaseConfig;
import com.saveur221.entities.Categorie;
import com.saveur221.entities.Client;
import com.saveur221.entities.Commande;
import com.saveur221.entities.LigneCommande;
import com.saveur221.entities.Produit;
import com.saveur221.enums.Statut;
import com.saveur221.interfaces.CommandeRepositoryInterface;

public class CommandeRepositoryImpl implements CommandeRepositoryInterface{
    private static final String SELECT_BASE =
        "SELECT co.*, cl.nom AS cl_nom, cl.prenom AS cl_prenom, " +
        "cl.telephone AS cl_telephone, cl.email AS cl_email " +
        "FROM commandes co JOIN clients cl ON co.client_id = cl.id ";

    private static final String SELECT_LIGNES =
        "SELECT lc.*, p.libelle AS p_libelle, p.description AS p_description, p.prix AS p_prix, " +
        "p.quantite_stock AS p_quantite_stock, p.temps_preparation AS p_temps_preparation, " +
        "p.calories AS p_calories, p.image AS p_image, p.seuil_alerte AS p_seuil_alerte, " +
        "p.categorie_id AS p_categorie_id, c.libelle AS c_libelle, c.description AS c_description " +
        "FROM ligne_commandes lc " +
        "JOIN produits p ON lc.produit_id = p.id " +
        "JOIN categories c ON p.categorie_id = c.id " +
        "WHERE lc.commande_id = ?";

    @Override
    public Optional<Commande> findById(Long id) {
        String sql = SELECT_BASE + "WHERE co.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Commande commande = hydrater(rs);
                commande.setLignes(chargerLignes(commande.getId(), conn));
                return Optional.of(commande);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Commande> findAll() {
        String sql = SELECT_BASE + "ORDER BY co.date_commande DESC";
        return executerListe(sql, stmt -> {});
    }

    @Override
    public List<Commande> findByStatut(Statut statut) {
        String sql = SELECT_BASE + "WHERE co.statut = ? ORDER BY co.date_commande DESC";
        return executerListe(sql, stmt -> stmt.setString(1, statut.name()));
    }

    @Override
    public List<Commande> findByClient(Long clientId) {
        String sql = SELECT_BASE + "WHERE co.client_id = ? ORDER BY co.date_commande DESC";
        return executerListe(sql, stmt -> stmt.setLong(1, clientId));
    }

    @Override
    public Commande save(Commande commande) {
        String sqlCommande = "INSERT INTO commandes (num_commande, date_commande, total, statut, client_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlLigne = "INSERT INTO ligne_commandes "
                + "(quantite, prix_unitaire, sous_total, produit_id, commande_id, instructions_speciales) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, commande.getNumCommande());
                stmt.setObject(2, commande.getDateCommande());
                stmt.setDouble(3, commande.getTotal());
                stmt.setString(4, commande.getStatut().name());
                stmt.setLong(5, commande.getClient().getId());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        commande.setId(keys.getLong(1));
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlLigne)) {
                for (LigneCommande ligne : commande.getLignes()) {
                    stmt.setInt(1, ligne.getQuantite());
                    stmt.setDouble(2, ligne.getPrixUnitaire());
                    stmt.setDouble(3, ligne.getSousTotal());
                    stmt.setLong(4, ligne.getProduit().getId());
                    stmt.setLong(5, commande.getId());
                    stmt.setString(6, ligne.getInstructionsSpeciales());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
            return commande;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save : " + e.getMessage(), e);
        }
    }

    @Override
    public Commande update(Commande commande) {
        // N'est utilise que pour changer le statut et le total -
        // les lignes ne sont jamais modifiees apres creation.
        String sql = "UPDATE commandes SET statut = ?, total = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, commande.getStatut().name());
            stmt.setDouble(2, commande.getTotal());
            stmt.setLong(3, commande.getId());

            stmt.executeUpdate();
            return commande;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update : " + e.getMessage(), e);
        }
    }

    private interface Parametreur {
        void appliquer(PreparedStatement stmt) throws SQLException;
    }

    private List<Commande> executerListe(String sql, Parametreur parametreur) {
        List<Commande> resultat = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            parametreur.appliquer(stmt);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = hydrater(rs);
                    commande.setLignes(chargerLignes(commande.getId(), conn));
                    resultat.add(commande);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur requete : " + e.getMessage(), e);
        }
        return resultat;
    }

    private List<LigneCommande> chargerLignes(Long commandeId, Connection conn) throws SQLException {
        List<LigneCommande> lignes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(SELECT_LIGNES)) {
            stmt.setLong(1, commandeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Categorie categorie = new Categorie(
                            rs.getLong("p_categorie_id"),
                            rs.getString("c_libelle"),
                            rs.getString("c_description"));

                    Produit produit = new Produit(
                            rs.getLong("produit_id"),
                            rs.getString("p_libelle"),
                            rs.getString("p_description"),
                            rs.getDouble("p_prix"),
                            rs.getInt("p_quantite_stock"),
                            categorie,
                            rs.getInt("p_temps_preparation"),
                            rs.getInt("p_calories"),
                            rs.getString("p_image"),
                            rs.getInt("p_seuil_alerte"));

                    lignes.add(new LigneCommande(
                            rs.getLong("id"),
                            rs.getInt("quantite"),
                            rs.getDouble("prix_unitaire"),
                            produit,
                            commandeId,
                            rs.getString("instructions_speciales")));
                }
            }
        }
        return lignes;
    }

    private Commande hydrater(ResultSet rs) throws SQLException {
        Client client = new Client(
                rs.getLong("client_id"),
                rs.getString("cl_nom"),
                rs.getString("cl_prenom"),
                rs.getString("cl_telephone"),
                rs.getString("cl_email"));

        return new Commande(
                rs.getLong("id"),
                rs.getString("num_commande"),
                rs.getTimestamp("date_commande").toLocalDateTime(),
                rs.getDouble("total"),
                Statut.valueOf(rs.getString("statut")),
                client);
    }

}
