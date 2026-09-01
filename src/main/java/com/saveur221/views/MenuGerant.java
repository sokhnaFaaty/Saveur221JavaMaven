package com.saveur221.views;

import java.util.Scanner;

import com.saveur221.services.CategorieService;
import com.saveur221.services.CommandeService;
import com.saveur221.services.PaiementService;
import com.saveur221.services.ProduitService;
import com.saveur221.services.StatistiqueService;

public class MenuGerant {
    private final CategorieView categorieView;
    private final ProduitView produitView;
    private final CommandeView commandeView;
    private final PaiementView paiementView;
    private final StatistiqueView statistiqueView;
    private final Scanner scanner;

    public MenuGerant(Scanner scanner, CategorieService categorieService, ProduitService produitService,
                       CommandeService commandeService, PaiementService paiementService,
                       StatistiqueService statistiqueService) {
        this.scanner = scanner;
        this.categorieView = new CategorieView(scanner, categorieService);
        this.produitView = new ProduitView(scanner, produitService);
        this.commandeView = new CommandeView(scanner, commandeService);
        this.paiementView = new PaiementView(scanner, paiementService);
        this.statistiqueView = new StatistiqueView(scanner, statistiqueService);
    }

    public void afficher() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n=== MENU GERANT ===");
            System.out.println("1. Categories");
            System.out.println("2. Produits");
            System.out.println("3. Commandes");
            System.out.println("4. Paiements");
            System.out.println("5. Statistiques");
            System.out.println("0. Deconnexion");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1" -> categorieView.afficherMenu();
                case "2" -> produitView.afficherMenu();
                case "3" -> commandeView.afficherMenu();
                case "4" -> paiementView.afficherMenu();
                case "5" -> statistiqueView.afficher();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

}
