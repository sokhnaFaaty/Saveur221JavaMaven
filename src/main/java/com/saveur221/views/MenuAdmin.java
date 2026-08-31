package com.saveur221.views;

import java.util.Scanner;

import com.saveur221.services.UtilisateurService;

public class MenuAdmin {
    private final Scanner scanner;
    private final UtilisateurView utilisateurView;

    public MenuAdmin(Scanner scanner, UtilisateurService utilisateurService) {
        this.scanner = scanner;
        this.utilisateurView = new UtilisateurView(scanner, utilisateurService);
    }

    public void afficher() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n=== MENU ADMIN ===");
            System.out.println("1. Utilisateurs");
            System.out.println("0. Deconnexion");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1" -> utilisateurView.afficherMenu();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }
}
