package com.saveur221.views;

import java.util.Scanner;

import com.saveur221.services.CategorieService;
import com.saveur221.services.CommandeService;
import com.saveur221.services.PaiementService;
import com.saveur221.services.ProduitService;
import com.saveur221.services.StatistiqueService;
import com.saveur221.services.UtilisateurService;

public class MenuAdmin {
    private final Scanner scanner;
    private final MenuGerant menuGerant;
    private final UtilisateurView utilisateurView;

    public MenuAdmin(Scanner scanner, CategorieService categorieService,
                     ProduitService produitService, CommandeService commandeService,
                     PaiementService paiementService, StatistiqueService statistiqueService,
                     UtilisateurService utilisateurService) {
        this.scanner = scanner;
        this.menuGerant = new MenuGerant(scanner, categorieService, produitService,
                commandeService, paiementService, statistiqueService);
        this.utilisateurView = new UtilisateurView(scanner, utilisateurService);
    }

    public void afficher() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n=== MENU ADMIN ===");
            System.out.println("1. Categories");
            System.out.println("2. Produits");
            System.out.println("3. Commandes");
            System.out.println("4. Paiements");
            System.out.println("5. Statistiques");
            System.out.println("6. Gestion des utilisateurs");
            System.out.println("0. Deconnexion");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1" -> menuGerant.getCategorieView().afficherMenu();
                case "2" -> menuGerant.getProduitView().afficherMenu();
                case "3" -> menuGerant.getCommandeView().afficherMenu();
                case "4" -> menuGerant.getPaiementView().afficherMenu();
                case "5" -> menuGerant.getStatistiqueView().afficher();
                case "6" -> utilisateurView.afficherMenu();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }
}
