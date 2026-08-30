package com.saveur221.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.saveur221.config.DatabaseConfig;
import com.saveur221.entities.Categorie;
import com.saveur221.exceptions.CategorieNonSupprimableException;
import com.saveur221.interfaces.CategorieRepositoryInterface;



public class CategorieRepositoryImpl implements CategorieRepositoryInterface{
    private static final String NON_SUPPRIME = " AND deleted_at IS NULL";

    @Override
    public Optional<Categorie> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?" + NON_SUPPRIME;
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
    public List<Categorie> findAll() {
        String sql = "SELECT * FROM categories WHERE deleted_at IS NULL ORDER BY libelle";
        List<Categorie> resultat = new ArrayList<>();

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
    public List<Categorie> search(String motCle) {
        String sql = "SELECT * FROM categories WHERE libelle ILIKE ?" + NON_SUPPRIME + " ORDER BY libelle";
        List<Categorie> resultat = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + motCle + "%");

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
    public boolean contientDesProduits(Long categorieId) {
        String sql = "SELECT COUNT(*) FROM produits WHERE categorie_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, categorieId);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur contientDesProduits : " + e.getMessage(), e);
        }
    }

    @Override
    public Categorie save(Categorie c) {
        String sql = "INSERT INTO categories (libelle, description) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, c.getLibelle());
            stmt.setString(2, c.getDescription());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getLong(1));
                }
            }
            return c;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save : " + e.getMessage(), e);
        }
    }

    @Override
    public Categorie update(Categorie c) {
        String sql = "UPDATE categories SET libelle = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getLibelle());
            stmt.setString(2, c.getDescription());
            stmt.setLong(3, c.getId());

            stmt.executeUpdate();
            return c;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        if (contientDesProduits(id)) {
            throw new CategorieNonSupprimableException(
                "Impossible de supprimer : cette categorie contient des produits.");
        }

        String sql = "UPDATE categories SET deleted_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete : " + e.getMessage(), e);
        }
    }

    private Categorie hydrater(ResultSet rs) throws SQLException {
        return new Categorie(
                rs.getLong("id"),
                rs.getString("libelle"),
                rs.getString("description")
        );
    }

    
}
