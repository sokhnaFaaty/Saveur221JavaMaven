package com.saveur221;

import java.sql.Connection;
import java.sql.SQLException;

import com.saveur221.config.DatabaseConfig;
import com.saveur221.interfaces.CategorieRepositoryInterface;
import com.saveur221.interfaces.CommandeRepositoryInterface;
import com.saveur221.interfaces.PaiementRepositoryInterface;
import com.saveur221.interfaces.ProduitRepositoryInterface;
import com.saveur221.interfaces.UtilisateurRepositoryInterface;
import com.saveur221.repository.CategorieRepositoryImpl;
import com.saveur221.repository.CommandeRepositoryImpl;
import com.saveur221.repository.PaiementRepositoryImpl;
import com.saveur221.repository.ProduitRepositoryImpl;
import com.saveur221.repository.UtilisateurRepositoryImpl;
import com.saveur221.services.AuthService;
import com.saveur221.services.CategorieService;
import com.saveur221.services.CommandeService;
import com.saveur221.services.PaiementService;
import com.saveur221.services.ProduitService;
import com.saveur221.services.StatistiqueService;
import com.saveur221.services.UtilisateurService;
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
        CategorieRepositoryInterface categorieRepository = new CategorieRepositoryImpl();
        ProduitRepositoryInterface produitRepository = new ProduitRepositoryImpl();
        CommandeRepositoryInterface commandeRepository = new CommandeRepositoryImpl();
        PaiementRepositoryInterface paiementRepository = new PaiementRepositoryImpl();


        AuthService authService = new AuthService(utilisateurRepository);
        CategorieService categorieService = new CategorieService(categorieRepository);
        ProduitService produitService = new ProduitService(produitRepository, categorieRepository);
        CommandeService commandeService = new CommandeService(commandeRepository, produitService);
        PaiementService paiementService = new PaiementService(paiementRepository, commandeService);
        StatistiqueService statistiqueService = new StatistiqueService(commandeRepository);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository,
                authService);


        new MenuPrincipal(authService, categorieService, produitService, commandeService,
                paiementService, statistiqueService, utilisateurService).demarrer();
    }

}
