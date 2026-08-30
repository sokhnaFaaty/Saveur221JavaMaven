package com.saveur221;

import java.sql.Connection;
import java.sql.SQLException;

import com.saveur221.config.DatabaseConfig;
import com.saveur221.interfaces.UtilisateurRepositoryInterface;
import com.saveur221.repository.UtilisateurRepositoryImpl;
import com.saveur221.services.AuthService;
import com.saveur221.views.MenuPrincipal;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SAVEUR221 - Application Java Console ===");

        try (Connection connection = DatabaseConfig.getConnection()) {
            System.out.println("Connexion a la base reussie : " + connection.getCatalog());
        } catch (SQLException e) {
            System.err.println("Echec de connexion a la base : " + e.getMessage());
            System.err.println("Verifie src/main/resources/application.properties");
            return;
        }

        UtilisateurRepositoryInterface utilisateurRepository = new UtilisateurRepositoryImpl();
        AuthService authService = new AuthService(utilisateurRepository);

        new MenuPrincipal(authService).demarrer();
    }

}
