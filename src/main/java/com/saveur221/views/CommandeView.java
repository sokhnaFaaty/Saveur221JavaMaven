package com.saveur221.views;

import java.util.List;
import java.util.Scanner;

import com.saveur221.entities.Commande;
import com.saveur221.entities.LigneCommande;
import com.saveur221.enums.Statut;
import com.saveur221.exceptions.SaveurException;
import com.saveur221.services.CommandeService;

public class CommandeView {
    private final Scanner scanner;
    private final CommandeService commandeService;

    public CommandeView(Scanner scanner, CommandeService commandeService) {
        this.scanner = scanner;
        this.commandeService = commandeService;
    }

    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- COMMANDES ---");
            System.out.println("1. Lister toutes");
            System.out.println("2. Filtrer par statut");
            System.out.println("3. Consulter le detail");
            System.out.println("4. Changer le statut");
            System.out.println("5. Annuler");
            System.out.println("0. Retour");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            try {
                switch (choix) {
                    case "1" -> afficherListe(commandeService.listerCommandes());
                    case "2" -> afficherListe(commandeService.listerParStatut(lireStatut()));
                    case "3" -> consulterDetail();
                    case "4" -> changerStatut();
                    case "5" -> annuler();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix invalide.");
                }
            } catch (SaveurException | IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void consulterDetail() {
        System.out.print("Id de la commande : ");
        Long id = lireId();
        Commande commande = commandeService.consulterCommande(id);
        System.out.println(commande);
        System.out.println("Client : " + commande.getClient());
        for (LigneCommande ligne : commande.getLignes()) {
            System.out.println("  - " + ligne);
        }
    }

    private void changerStatut() {
        System.out.print("Id de la commande : ");
        Long id = lireId();
        Commande commande = commandeService.changerStatut(id, lireStatut());
        System.out.println("Statut mis a jour : " + commande);
    }

    private void annuler() {
        System.out.print("Id de la commande a annuler : ");
        Long id = lireId();
        Commande commande = commandeService.annulerCommande(id);
        System.out.println("Commande annulee, stock restaure : " + commande);
    }

    private void afficherListe(List<Commande> commandes) {
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande.");
            return;
        }
        commandes.forEach(System.out::println);
    }

    private Statut lireStatut() {
        System.out.println("Statuts possibles : EN_ATTENTE, EN_PREPARATION, PRETE, RETIREE, ANNULEE");
        System.out.print("Statut : ");
        return Statut.valueOf(scanner.nextLine().trim().toUpperCase());
    }

    private Long lireId() {
        return Long.parseLong(scanner.nextLine().trim());
    }

}
