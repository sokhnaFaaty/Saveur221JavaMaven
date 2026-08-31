package com.saveur221.views;

import java.util.List;
import java.util.Scanner;

import com.saveur221.entities.Commande;
import com.saveur221.entities.Paiement;
import com.saveur221.enums.Etat;
import com.saveur221.exceptions.SaveurException;
import com.saveur221.services.PaiementService;

public class PaiementView {
    private final Scanner scanner;
    private final PaiementService paiementService;

    public PaiementView(Scanner scanner, PaiementService paiementService) {
        this.scanner = scanner;
        this.paiementService = paiementService;
    }

    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- PAIEMENTS ---");
            System.out.println("1. Enregistrer un paiement");
            System.out.println("2. Lister les paiements d'une commande");
            System.out.println("3. Voir le montant restant d'une commande");
            System.out.println("4. Commandes impayees");
            System.out.println("5. Commandes partiellement payees");
            System.out.println("0. Retour");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            try {
                switch (choix) {
                    case "1" -> enregistrer();
                    case "2" -> listerPourCommande();
                    case "3" -> montantRestant();
                    case "4" -> afficherListe(paiementService.listerCommandesImpayees());
                    case "5" -> afficherListe(paiementService.listerCommandesPartiellementPayees());
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix invalide.");
                }
            } catch (SaveurException | IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void enregistrer() {
        System.out.print("Id de la commande : ");
        Long commandeId = lireId();
        System.out.print("Montant : ");
        double montant = lireDouble();
        Paiement paiement = paiementService.enregistrerPaiement(commandeId, montant);
        System.out.println("Paiement enregistre : " + paiement);
    }

    private void listerPourCommande() {
        System.out.print("Id de la commande : ");
        Long commandeId = lireId();
        List<Paiement> paiements = paiementService.listerPaiementsCommande(commandeId);
        if (paiements.isEmpty()) {
            System.out.println("Aucun paiement pour cette commande.");
            return;
        }
        paiements.forEach(System.out::println);
    }

    private void montantRestant() {
        System.out.print("Id de la commande : ");
        Long commandeId = lireId();
        double restant = paiementService.montantRestant(commandeId);
        System.out.printf("Montant restant a payer : %.0f%n", restant);
    }

    private void afficherListe(List<Commande> commandes) {
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande dans cette categorie.");
            return;
        }
        commandes.forEach(System.out::println);
    }

    private Long lireId() {
        return Long.parseLong(scanner.nextLine().trim());
    }

    private double lireDouble() {
        return Double.parseDouble(scanner.nextLine().trim());
    }

}
