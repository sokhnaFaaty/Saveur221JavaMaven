package com.saveur221.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.saveur221.config.DatabaseConfig;
import com.saveur221.entities.Categorie;
import com.saveur221.entities.Produit;
import com.saveur221.interfaces.ProduitRepositoryInterface;

public class ProduitRepositoryImpl implements ProduitRepositoryInterface {

    private static final String NON_SUPPRIME = " AND p.deleted_at IS NULL";

    // Jointure avec categories pour recuperer libelle et description de la
    // categorie
    // en meme temps que le produit, en une seule requete.
    private static final String SELECT_BASE = "SELECT p.*, c.libelle AS cat_libelle, c.description AS cat_description "
            +
            "FROM produits p JOIN categories c ON p.categorie_id = c.id ";

    @Override
    public Optional<Produit> findById(Long id) {
        String sql = SELECT_BASE + "WHERE p.id = ?" + NON_SUPPRIME;
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(hydrater(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Produit> findByLibelle(String libelle) {
        String sql = SELECT_BASE + "WHERE p.libelle = ?" + NON_SUPPRIME;
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, libelle);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(hydrater(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByLibelle : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produit> findAll() {
        String sql = SELECT_BASE + "WHERE p.deleted_at IS NULL ORDER BY p.libelle";
        return executerListe(sql, stmt -> {
        });
    }

    @Override
    public List<Produit> findByCategorie(Long categorieId) {
        String sql = SELECT_BASE + "WHERE p.categorie_id = ?" + NON_SUPPRIME + " ORDER BY p.libelle";
        return executerListe(sql, stmt -> stmt.setLong(1, categorieId));
    }

    @Override
    public List<Produit> search(String motCle) {
        String sql = SELECT_BASE + "WHERE p.libelle ILIKE ?" + NON_SUPPRIME + " ORDER BY p.libelle";
        return executerListe(sql, stmt -> stmt.setString(1, "%" + motCle + "%"));
    }

    @Override
    public List<Produit> findEnRupture() {
        String sql = SELECT_BASE + "WHERE p.quantite_stock <= 0" + NON_SUPPRIME + " ORDER BY p.libelle";
        return executerListe(sql, stmt -> {
        });
    }

    @Override
    public List<Produit> findStockFaible() {
        String sql = SELECT_BASE + "WHERE p.quantite_stock > 0 AND p.quantite_stock <= p.seuil_alerte"
                + NON_SUPPRIME + " ORDER BY p.libelle";
        return executerListe(sql, stmt -> {
        });
    }

    @Override
    public Produit save(Produit p) {
        String sql = "INSERT INTO produits (libelle, description, prix, quantite_stock, categorie_id,temps_preparation, calories, image, seuil_alerte) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?,?,?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getLibelle());
            stmt.setString(2, p.getDescription());
            stmt.setDouble(3, p.getPrix());
            stmt.setInt(4, p.getQuantiteStock());
            stmt.setLong(5, p.getCategorie().getId());
            stmt.setInt(6, p.getTempsPreparation());
            stmt.setInt(7, p.getCalories());
            stmt.setString(8, p.getImage());
            stmt.setInt(9, p.getSeuilAlerte());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getLong(1));
                }
            }
            return p;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save : " + e.getMessage(), e);
        }
    }

    @Override
    public Produit update(Produit p) {
        String sql = "UPDATE produits SET libelle = ?, description = ?, prix = ?, quantite_stock = ?, "
                + "categorie_id = ?, temps_preparation = ?, calories = ?, image = ?, seuil_alerte = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getLibelle());
            stmt.setString(2, p.getDescription());
            stmt.setDouble(3, p.getPrix());
            stmt.setInt(4, p.getQuantiteStock());
            stmt.setLong(5, p.getCategorie().getId());
            stmt.setInt(6, p.getTempsPreparation());
            stmt.setInt(7, p.getCalories());
            stmt.setString(8, p.getImage());
            stmt.setInt(9, p.getSeuilAlerte());
            stmt.setLong(10, p.getId());

            stmt.executeUpdate();
            return p;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "UPDATE produits SET deleted_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete : " + e.getMessage(), e);
        }
    }

    // Petit raccourci maison pour eviter de repeter le meme bloc try/catch
    // dans findAll, findByCategorie, search, findEnRupture, findStockFaible.
    private interface Parametreur {
        void appliquer(PreparedStatement stmt) throws SQLException;
    }

    private List<Produit> executerListe(String sql, Parametreur parametreur) {
        List<Produit> resultat = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            parametreur.appliquer(stmt);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultat.add(hydrater(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur requete : " + e.getMessage(), e);
        }
        return resultat;
    }

    private Produit hydrater(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie(
                rs.getLong("categorie_id"),
                rs.getString("cat_libelle"),
                rs.getString("cat_description"));

        return new Produit(
                rs.getLong("id"),
                rs.getString("libelle"),
                rs.getString("description"),
                rs.getDouble("prix"),
                rs.getInt("quantite_stock"),
                categorie,
                rs.getInt("temps_preparation"),
                rs.getInt("calories"),
                rs.getString("image"),
                rs.getInt("seuil_alerte"));
    }
}