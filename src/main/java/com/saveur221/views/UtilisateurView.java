package com.saveur221.views;

import java.util.List;
import java.util.Scanner;

import com.saveur221.entities.Utilisateur;
import com.saveur221.enums.Role;
import com.saveur221.exceptions.SaveurException;
import com.saveur221.services.UtilisateurService;

public class UtilisateurView {
    private final Scanner scanner;
    private final UtilisateurService utilisateurService;

    public UtilisateurView(Scanner scanner, UtilisateurService utilisateurService) {
        this.scanner = scanner;
        this.utilisateurService = utilisateurService;
    }

    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- UTILISATEURS ---");
            System.out.println("1. Lister");
            System.out.println("2. Ajouter");
            System.out.println("3. Rechercher");
            System.out.println("4. Modifier");
            System.out.println("5. Supprimer");
            System.out.println("6. Activer / Desactiver");
            System.out.println("7. Changer le role");
            System.out.println("0. Retour");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();

            try {
                switch (choix) {
                    case "1" -> afficherListe(utilisateurService.listerUtilisateurs());
                    case "2" -> ajouter();
                    case "3" -> rechercher();
                    case "4" -> modifier();
                    case "5" -> supprimer();
                    case "6" -> activerDesactiver();
                    case "7" -> changerRole();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix invalide.");
                }
            } catch (SaveurException | IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void ajouter() {
        System.out.print("Nom : ");
        String nom = scanner.nextLine();
        System.out.print("Prenom : ");
        String prenom = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Telephone : ");
        String telephone = scanner.nextLine();
        System.out.print("Mot de passe (min 6 caracteres) : ");
        String motDePasse = scanner.nextLine();
        System.out.println("Role (ADMIN ou GERANT) : ");
        Role role = lireRole();
        System.out.print("Image : ");
        String image = scanner.nextLine();

        Utilisateur utilisateur = utilisateurService.ajouterUtilisateur(nom, prenom, email,
                motDePasse, telephone, role, image);
        System.out.println("Utilisateur cree : " + utilisateur);
    }

    private void rechercher() {
        System.out.print("Mot-cle : ");
        String motCle = scanner.nextLine();
        afficherListe(utilisateurService.rechercherUtilisateurs(motCle));
    }

    private void modifier() {
        System.out.print("Id de l'utilisateur a modifier : ");
        Long id = lireId();
        System.out.print("Nom : ");
        String nom = scanner.nextLine();
        System.out.print("Prenom : ");
        String prenom = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Telephone : ");
        String telephone = scanner.nextLine();
        System.out.print("Image : ");
        String image = scanner.nextLine();

        Utilisateur utilisateur = utilisateurService.modifierUtilisateur(id, nom, prenom, email,
                telephone, image);
        System.out.println("Utilisateur modifie : " + utilisateur);
    }

    private void supprimer() {
        System.out.print("Id de l'utilisateur a supprimer : ");
        Long id = lireId();
        utilisateurService.supprimerUtilisateur(id);
        System.out.println("Utilisateur supprime.");
    }

    private void activerDesactiver() {
        System.out.print("Id de l'utilisateur : ");
        Long id = lireId();
        System.out.print("Activer (true) ou desactiver (false) ? ");
        boolean actif = Boolean.parseBoolean(scanner.nextLine().trim());
        Utilisateur utilisateur = utilisateurService.activerDesactiver(id, actif);
        System.out.println("Statut mis a jour : " + utilisateur);
    }

    private void changerRole() {
        System.out.print("Id de l'utilisateur : ");
        Long id = lireId();
        System.out.println("Nouveau role (ADMIN ou GERANT) : ");
        Role role = lireRole();
        Utilisateur utilisateur = utilisateurService.changerRole(id, role);
        System.out.println("Role mis a jour : " + utilisateur);
    }

    private void afficherListe(List<Utilisateur> utilisateurs) {
        if (utilisateurs.isEmpty()) {
            System.out.println("Aucun utilisateur.");
            return;
        }
        utilisateurs.forEach(System.out::println);
    }

    private Role lireRole() {
        return Role.valueOf(scanner.nextLine().trim().toUpperCase());
    }

    private Long lireId() {
        return Long.parseLong(scanner.nextLine().trim());
    }
}
