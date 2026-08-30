package com.saveur221.views;

import java.util.Scanner;

import com.saveur221.entities.Utilisateur;
import com.saveur221.exceptions.SaveurException;
import com.saveur221.services.AuthService;

public class MenuPrincipal {
    private final AuthService authService;
    private final Scanner scanner;

    public MenuPrincipal(AuthService authService) {
        this.authService = authService;
        this.scanner = new Scanner(System.in);
    }

    public void demarrer() {
        System.out.println("=== SAVEUR221 - Connexion ===");

        Utilisateur utilisateurConnecte = null;

        while (utilisateurConnecte == null) {
            System.out.print("Email : ");
            String email = scanner.nextLine().trim();

            System.out.print("Mot de passe : ");
            String motDePasse = scanner.nextLine();

            try {
                utilisateurConnecte = authService.authentifier(email, motDePasse);
            } catch (SaveurException e) {
                System.out.println("Erreur : " + e.getMessage());
                System.out.println("Reessayez.\n");
            }
        }

        afficherMenu(utilisateurConnecte);
    }

    private void afficherMenu(Utilisateur utilisateur) {
        System.out.printf("%nBienvenue %s %s !%n", utilisateur.getPrenom(), utilisateur.getNom());

        if (authService.estAdmin(utilisateur)) {
            System.out.println("-> Redirection vers le menu Administrateur");
            System.out.println("[MenuAdmin pas encore implemente]");
        } else if (authService.estGerant(utilisateur)) {
            System.out.println("-> Redirection vers le menu Gerant");
            System.out.println("[MenuGerant pas encore implemente]");
        }
    }

}
