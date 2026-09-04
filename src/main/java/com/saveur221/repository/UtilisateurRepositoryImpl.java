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
import com.saveur221.entities.Utilisateur;
import com.saveur221.enums.Role;
import com.saveur221.interfaces.UtilisateurRepositoryInterface;

public class UtilisateurRepositoryImpl implements UtilisateurRepositoryInterface {
    // Condition pour ignorer les utilisateurs supprimés
    private static final String NON_SUPPRIME = " AND deleted_at IS NULL";

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?" + NON_SUPPRIME;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Utilisateur trouvé
                    return Optional.of(hydrater(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByEmail : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Utilisateur> findByTelephone(String telephone) {
        String sql = "SELECT * FROM utilisateurs WHERE telephone = ?" + NON_SUPPRIME;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, telephone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(hydrater(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByTelephone : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?" + NON_SUPPRIME;
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
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM utilisateurs WHERE deleted_at IS NULL ORDER BY nom";
        List<Utilisateur> resultat = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                resultat.add(hydrater(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll : " + e.getMessage(), e);
        }
        return resultat;
    }

    @Override
    public List<Utilisateur> search(String motCle) {
        String sql = "SELECT * FROM utilisateurs WHERE (nom ILIKE ? OR prenom ILIKE ?)"
                + NON_SUPPRIME + " ORDER BY nom";
        List<Utilisateur> resultat = new ArrayList<>();
        String pattern = "%" + motCle + "%";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultat.add(hydrater(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur search : " + e.getMessage(), e);
        }
        return resultat;
    }

    @Override
    public Utilisateur save(Utilisateur u) {
        String sql = """
                INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, telephone, role, actif, image)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             // Pour récupérer l'ID auto-généré par la base
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, u.getNom());
            stmt.setString(2, u.getPrenom());
            stmt.setString(3, u.getEmail());
            stmt.setString(4, u.getMotDePasse());
            stmt.setString(5, u.getTelephone());
            stmt.setString(6, u.getRole().name());
            stmt.setBoolean(7, u.isActif());
            stmt.setString(8, u.getImage());

            stmt.executeUpdate();

            // On récupère le nouvel ID et on l'affecte à l'objet
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setId(keys.getLong(1));
                }
            }
            return u;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save : " + e.getMessage(), e);
        }
    }

    @Override
    public Utilisateur update(Utilisateur u) {
        String sql = """
                UPDATE utilisateurs
                SET nom = ?, prenom = ?, email = ?, telephone = ?, role = ?, actif = ?, image = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNom());
            stmt.setString(2, u.getPrenom());
            stmt.setString(3, u.getEmail());
            stmt.setString(4, u.getTelephone());
            stmt.setString(5, u.getRole().name());
            stmt.setBoolean(6, u.isActif());
            stmt.setString(7, u.getImage());
            stmt.setLong(8, u.getId());

            stmt.executeUpdate();
            return u;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        // Soft delete : on met juste à jour la date de suppression
        String sql = "UPDATE utilisateurs SET deleted_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete : " + e.getMessage(), e);
        }
    }

    // Méthode pour transformer une ligne SQL en objet Utilisateur
    private Utilisateur hydrater(ResultSet rs) throws SQLException {
        return new Utilisateur(
                rs.getLong("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getString("telephone"),
                Role.valueOf(rs.getString("role")),
                rs.getBoolean("actif"),
                rs.getString("image")
        );
    }
}
