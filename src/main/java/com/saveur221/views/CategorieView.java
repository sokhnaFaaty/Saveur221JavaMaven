package com.saveur221.views;

import java.util.List;
import java.util.Scanner;

import com.saveur221.entities.Categorie;
import com.saveur221.exceptions.SaveurException;
import com.saveur221.services.CategorieService;

public class CategorieView {
    private final Scanner scanner;
    private final CategorieService categorieService;

    public CategorieView(Scanner scanner, CategorieService categorieService) {
        this.scanner = scanner;
        this.categorieService = categorieService;
    }

    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- CATEGORIES ---");
            System.out.println("1. Lister");
            System.out.println("2. Ajouter");
            System.out.println("3. Modifier");
            System.out.println("4. Supprimer");
            System.out.println("5. Rechercher");
            System.out.println("6. Corbeille");
            System.out.println("0. Retour");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            try {
                switch (choix) {
                    case "1" -> afficherListe(categorieService.listerCategories());
                    case "2" -> ajouter();
                    case "3" -> modifier();
                    case "4" -> supprimer();
                    case "5" -> rechercher();
                    case "6" -> corbeille();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix invalide.");
                }
            } catch (SaveurException | IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void corbeille() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- CORBEILLE CATEGORIES ---");
            System.out.println("1. Lister les categories supprimees");
            System.out.println("2. Restaurer une categorie");
            System.out.println("3. Purger une categorie (definitif)");
            System.out.println("0. Retour");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            try {
                switch (choix) {
                    case "1" -> afficherListe(categorieService.listerCategoriesSupprimees());
                    case "2" -> restaurerCorbeille();
                    case "3" -> purgerCorbeille();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix invalide.");
                }
            } catch (SaveurException | IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void restaurerCorbeille() {
        System.out.print("Id de la categorie a restaurer : ");
        Long id = lireId();
        Categorie categorie = categorieService.restaurerCategorie(id);
        System.out.println("Categorie restauree : " + categorie);
    }

    private void purgerCorbeille() {
        System.out.print("Id de la categorie a purger : ");
        Long id = lireId();
        categorieService.purgerCategorie(id);
        System.out.println("Categorie purgee definitivement.");
    }

    private void ajouter() {
        System.out.print("Libelle : ");
        String libelle = scanner.nextLine();
        System.out.print("Description : ");
        String description = scanner.nextLine();

        Categorie categorie = categorieService.ajouterCategorie(libelle, description);
        System.out.println("Categorie creee : " + categorie);
    }

    private void modifier() {
        System.out.print("Id de la categorie a modifier : ");
        Long id = lireId();
        System.out.print("Nouveau libelle : ");
        String libelle = scanner.nextLine();
        System.out.print("Nouvelle description : ");
        String description = scanner.nextLine();

        Categorie categorie = categorieService.modifierCategorie(id, libelle, description);
        System.out.println("Categorie modifiee : " + categorie);
    }

    private void supprimer() {
        System.out.print("Id de la categorie a supprimer : ");
        Long id = lireId();
        categorieService.supprimerCategorie(id);
        System.out.println("Categorie supprimee.");
    }

    private void rechercher() {
        System.out.print("Mot-cle : ");
        String motCle = scanner.nextLine();
        afficherListe(categorieService.rechercherCategorie(motCle));
    }

    private void afficherListe(List<Categorie> categories) {
        if (categories.isEmpty()) {
            System.out.println("Aucune categorie.");
            return;
        }
        categories.forEach(System.out::println);
    }

    private Long lireId() {
        return Long.parseLong(scanner.nextLine().trim());
    }

}
