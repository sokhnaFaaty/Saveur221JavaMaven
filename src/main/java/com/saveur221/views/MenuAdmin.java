package com.saveur221.views;

import java.util.Scanner;

import com.saveur221.services.CategorieService;
import com.saveur221.services.CommandeService;
import com.saveur221.services.PaiementService;
import com.saveur221.services.ProduitService;
import com.saveur221.services.UtilisateurService;

public class MenuAdmin {
    private final Scanner scanner;
    private final MenuGerant menuGerant;
    private final UtilisateurView utilisateurView;

    public MenuAdmin(Scanner scanner, CategorieService categorieService,
                     ProduitService produitService, CommandeService commandeService,
                     PaiementService paiementService, UtilisateurService utilisateurService) {
        this.scanner = scanner;
        this.menuGerant = new MenuGerant(scanner, categorieService, produitService,
                commandeService, paiementService);
        this.utilisateurView = new UtilisateurView(scanner, utilisateurService);
    }

    public void afficher() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n=== MENU ADMIN ===");
            System.out.println("1. Toutes les fonctions du gerant");
            System.out.println("2. Gestion des utilisateurs");
            System.out.println("0. Deconnexion");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1" -> menuGerant.afficher();
                case "2" -> utilisateurView.afficherMenu();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }
}
