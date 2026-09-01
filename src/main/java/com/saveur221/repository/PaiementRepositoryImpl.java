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
import com.saveur221.entities.Paiement;
import com.saveur221.interfaces.PaiementRepositoryInterface;

public class PaiementRepositoryImpl implements PaiementRepositoryInterface {
    
    public Optional<Paiement> findById(Long id) {
        String sql = "SELECT * FROM paiements WHERE id = ?";
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
    public List<Paiement> findByCommande(Long commandeId) {
        String sql = "SELECT * FROM paiements WHERE commande_id = ? ORDER BY date_paiement";
        List<Paiement> resultat = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, commandeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultat.add(hydrater(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCommande : " + e.getMessage(), e);
        }
        return resultat;
    }

    @Override
    public double sommePaiements(Long commandeId) {
        String sql = "SELECT COALESCE(SUM(montant), 0) FROM paiements WHERE commande_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, commandeId);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur sommePaiements : " + e.getMessage(), e);
        }
    }

    @Override
    public Paiement save(Paiement p) {
        String sql = "INSERT INTO paiements (montant, date_paiement, commande_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, p.getMontant());
            stmt.setObject(2, p.getDatePaiement());
            stmt.setLong(3, p.getCommandeId());

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

    private Paiement hydrater(ResultSet rs) throws SQLException {
        return new Paiement(
                rs.getLong("id"),
                rs.getDouble("montant"),
                rs.getTimestamp("date_paiement").toLocalDateTime(),
                rs.getLong("commande_id"));
    }
}
