package com.saveur221.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.saveur221.config.DatabaseConfig;
import com.saveur221.entities.Client;
import com.saveur221.interfaces.ClientRepositoryInterface;

public class ClientRepositoryImpl implements ClientRepositoryInterface {
    @Override
    public Optional<Client> findById(Long id) {
        String sql = "SELECT * FROM clients WHERE id = ? AND deleted_at IS NULL";
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

    private Client hydrater(ResultSet rs) throws SQLException {
        return new Client(
                rs.getLong("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("email"));
    }

}
