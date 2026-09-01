package com.saveur221.views;

import java.util.List;
import java.util.Scanner;

import com.saveur221.entities.Produit;
import com.saveur221.exceptions.SaveurException;
import com.saveur221.services.ProduitService;

public class ProduitView {
    private final Scanner scanner;
    private final ProduitService produitService;

    public ProduitView(Scanner scanner, ProduitService produitService) {
        this.scanner = scanner;
        this.produitService = produitService;
    }

    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- PRODUITS ---");
            System.out.println("1. Lister");
            System.out.println("2. Ajouter");
            System.out.println("3. Modifier");
            System.out.println("4. Supprimer");
            System.out.println("5. Rechercher");
            System.out.println("6. Filtrer par categorie");
            System.out.println("7. Approvisionner");
            System.out.println("8. Definir seuil d'alerte");
            System.out.println("9. Produits en rupture");
            System.out.println("10. Produits en stock faible");
            System.out.println("11. Produits disponibles");
            System.out.println("12. Produits indisponibles");
            System.out.println("0. Retour");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            try {
                switch (choix) {
                    case "1" -> afficherListe(produitService.listerProduits());
                    case "2" -> ajouter();
                    case "3" -> modifier();
                    case "4" -> supprimer();
                    case "5" -> rechercher();
                    case "6" -> filtrerParCategorie();
                    case "7" -> approvisionner();
                    case "8" -> definirSeuil();
                    case "9" -> afficherListe(produitService.listerProduitsEnRupture());
                    case "10" -> afficherListe(produitService.listerProduitsStockFaible());
                    case "11" -> afficherListe(produitService.listerProduitsDisponibles());
                    case "12" -> afficherListe(produitService.listerProduitsIndisponibles());
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix invalide.");
                }
            } catch (SaveurException | IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void ajouter() {
        System.out.print("Libelle : ");
        String libelle = scanner.nextLine();
        System.out.print("Description : ");
        String description = scanner.nextLine();
        System.out.print("Prix : ");
        double prix = lireDouble();
        System.out.print("Quantite en stock : ");
        int quantiteStock = lireInt();
        System.out.print("Id de la categorie : ");
        Long categorieId = lireId();
        System.out.print("Temps de preparation (min) : ");
        int tempsPreparation = lireInt();
        System.out.print("Calories : ");
        int calories = lireInt();
        System.out.print("Image (vide si aucune) : ");
        String image = scanner.nextLine();
        System.out.print("Seuil d'alerte : ");
        int seuilAlerte = lireInt();

        Produit produit = produitService.ajouterProduit(libelle, description, prix, quantiteStock,
                categorieId, tempsPreparation, calories, image, seuilAlerte);
        System.out.println("Produit cree : " + produit);
    }

    private void modifier() {
        System.out.print("Id du produit a modifier : ");
        Long id = lireId();
        System.out.print("Nouveau libelle : ");
        String libelle = scanner.nextLine();
        System.out.print("Nouvelle description : ");
        String description = scanner.nextLine();
        System.out.print("Nouveau prix : ");
        double prix = lireDouble();
        System.out.print("Nouvel id de categorie : ");
        Long categorieId = lireId();
        System.out.print("Nouveau temps de preparation (min) : ");
        int tempsPreparation = lireInt();
        System.out.print("Nouvelles calories : ");
        int calories = lireInt();
        System.out.print("Nouvelle image : ");
        String image = scanner.nextLine();
        System.out.print("Nouveau seuil d'alerte : ");
        int seuilAlerte = lireInt();

        Produit produit = produitService.modifierProduit(id, libelle, description, prix,
                categorieId, tempsPreparation, calories, image, seuilAlerte);
        System.out.println("Produit modifie : " + produit);
    }

    private void supprimer() {
        System.out.print("Id du produit a supprimer : ");
        Long id = lireId();
        produitService.supprimerProduit(id);
        System.out.println("Produit supprime.");
    }

    private void rechercher() {
        System.out.print("Mot-cle : ");
        String motCle = scanner.nextLine();
        afficherListe(produitService.rechercherProduit(motCle));
    }

    private void filtrerParCategorie() {
        System.out.print("Id de la categorie : ");
        Long categorieId = lireId();
        afficherListe(produitService.listerParCategorie(categorieId));
    }

    private void approvisionner() {
        System.out.print("Id du produit : ");
        Long id = lireId();
        System.out.print("Quantite a ajouter : ");
        int quantite = lireInt();
        Produit produit = produitService.approvisionner(id, quantite);
        System.out.println("Stock mis a jour : " + produit);
    }

    private void definirSeuil() {
        System.out.print("Id du produit : ");
        Long id = lireId();
        System.out.print("Nouveau seuil : ");
        int seuil = lireInt();
        Produit produit = produitService.definirSeuilAlerte(id, seuil);
        System.out.println("Seuil mis a jour : " + produit);
    }

    private void afficherListe(List<Produit> produits) {
        if (produits.isEmpty()) {
            System.out.println("Aucun produit.");
            return;
        }
        produits.forEach(System.out::println);
    }

    private Long lireId() {
        return Long.parseLong(scanner.nextLine().trim());
    }

    private int lireInt() {
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private double lireDouble() {
        return Double.parseDouble(scanner.nextLine().trim());
    }

}
