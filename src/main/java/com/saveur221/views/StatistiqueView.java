package com.saveur221.views;

import java.util.Map;
import java.util.Scanner;

import com.saveur221.entities.Produit;
import com.saveur221.services.StatistiqueService;

public class StatistiqueView {
    private final Scanner scanner;
    private final StatistiqueService statistiqueService;

    public StatistiqueView(Scanner scanner, StatistiqueService statistiqueService) {
        this.scanner = scanner;
        this.statistiqueService = statistiqueService;
    }

    public void afficher() {
        System.out.println("\n--- STATISTIQUES ---");

        System.out.printf("Chiffre d'affaires du jour       : %.0f FCFA%n",
                statistiqueService.chiffreAffairesDuJour());
        System.out.printf("Chiffre d'affaires de la semaine  : %.0f FCFA%n",
                statistiqueService.chiffreAffairesDeLaSemaine());
        System.out.printf("Chiffre d'affaires du mois        : %.0f FCFA%n",
                statistiqueService.chiffreAffairesDuMois());

        System.out.printf("Nombre de commandes               : %d%n",
                statistiqueService.nombreCommandes());
        System.out.printf("Commandes en cours                : %d%n",
                statistiqueService.nombreCommandesEnCours());

        Produit plusVendu = statistiqueService.produitLePlusVendu();
        System.out.println();
        System.out.println("Produit le plus vendu : " +
                (plusVendu != null ? plusVendu.getLibelle() : "aucun"));

        System.out.println();
        System.out.println("Top 3 des produits :");
        Map<Produit, Integer> top = statistiqueService.topProduitsAvecQuantite();
        if (top.isEmpty()) {
            System.out.println("  Aucune vente.");
        } else {
            int rang = 1;
            for (Map.Entry<Produit, Integer> entree : top.entrySet()) {
                Produit produit = entree.getKey();
                String libelle = produit != null ? produit.getLibelle() : "?";
                System.out.printf("  %d. %s (%d vendus)%n", rang++, libelle, entree.getValue());
            }
        }
        System.out.println();
        System.out.println("Appuyez sur Entree pour continuer...");
        scanner.nextLine();
    }
}
