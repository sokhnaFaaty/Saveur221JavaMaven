package com.saveur221.views;

import java.util.Scanner;

import com.saveur221.entities.Utilisateur;
import com.saveur221.exceptions.SaveurException;
import com.saveur221.services.AuthService;
import com.saveur221.services.CategorieService;
import com.saveur221.services.CommandeService;
import com.saveur221.services.PaiementService;
import com.saveur221.services.ProduitService;
import com.saveur221.services.UtilisateurService;

public class MenuPrincipal{
    private final AuthService authService;
    private final CategorieService categorieService;
    private final ProduitService produitService;
    private final CommandeService commandeService;
    private final PaiementService paiementService;
    private final UtilisateurService utilisateurService;
    private final Scanner scanner;

    public MenuPrincipal(AuthService authService, CategorieService categorieService,
                          ProduitService produitService, CommandeService commandeService,
                          PaiementService paiementService, UtilisateurService utilisateurService) {
        this.authService = authService;
        this.categorieService = categorieService;
        this.produitService = produitService;
        this.commandeService = commandeService;
        this.paiementService = paiementService;
        this.utilisateurService = utilisateurService;
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
            new MenuAdmin(scanner, utilisateurService).afficher();
        } else if (authService.estGerant(utilisateur)) {
            new MenuGerant(scanner, categorieService, produitService, commandeService, paiementService)
                    .afficher();
        } else {
            System.out.println("Ce compte n'a pas les droits necessaires.");
        }
    }

}