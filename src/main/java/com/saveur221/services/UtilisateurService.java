package com.saveur221.services;

import java.util.List;
import java.util.Optional;

import com.saveur221.entities.Utilisateur;
import com.saveur221.enums.Role;
import com.saveur221.exceptions.EmailDejaUtiliseException;
import com.saveur221.exceptions.TelephoneDejaUtiliseException;
import com.saveur221.exceptions.UtilisateurInexistantException;
import com.saveur221.interfaces.UtilisateurRepositoryInterface;

public class UtilisateurService {
    private final UtilisateurRepositoryInterface utilisateurRepository;
    private final AuthService authService;

    public UtilisateurService(UtilisateurRepositoryInterface utilisateurRepository,
                              AuthService authService) {
        this.utilisateurRepository = utilisateurRepository;
        this.authService = authService;
    }

    public Utilisateur ajouterUtilisateur(String nom, String prenom, String email,
                                          String motDePasseClair, String telephone,
                                          Role role, String image) {
        verifierEmailDisponible(email);
        verifierTelephoneDisponible(telephone);

        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()) {
            throw new IllegalArgumentException("Le nom et le prenom sont obligatoires.");
        }
        if (!authService.motDePasseValide(motDePasseClair)) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caracteres.");
        }

        String hash = authService.hasherMotDePasse(motDePasseClair);
        Utilisateur utilisateur = new Utilisateur(null, nom, prenom, email, hash, telephone,
                role, true, image);
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public List<Utilisateur> rechercherUtilisateurs(String motCle) {
        return utilisateurRepository.search(motCle);
    }

    public Utilisateur modifierUtilisateur(Long id, String nom, String prenom, String email,
                                           String telephone, String image) {
        Utilisateur utilisateur = getUtilisateur(id);

        String nouvelEmail = (email == null || email.isBlank()) ? utilisateur.getEmail() : email;
        if (!nouvelEmail.equals(utilisateur.getEmail())) {
            verifierEmailDisponible(nouvelEmail);
        }

        String nouveauTelephone = (telephone == null || telephone.isBlank())
                ? utilisateur.getTelephone() : telephone;
        if (!nouveauTelephone.equals(utilisateur.getTelephone())) {
            verifierTelephoneDisponible(nouveauTelephone);
        }

        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(nouvelEmail);
        utilisateur.setTelephone(nouveauTelephone);
        utilisateur.setImage(image);

        return utilisateurRepository.update(utilisateur);
    }

    public void supprimerUtilisateur(Long id) {
        getUtilisateur(id);
        utilisateurRepository.delete(id);
    }

    public Utilisateur activerDesactiver(Long id, boolean actif) {
        Utilisateur utilisateur = getUtilisateur(id);
        utilisateur.setActif(actif);
        return utilisateurRepository.update(utilisateur);
    }

    public Utilisateur changerRole(Long id, Role role) {
        Utilisateur utilisateur = getUtilisateur(id);
        utilisateur.setRole(role);
        return utilisateurRepository.update(utilisateur);
    }

    private Utilisateur getUtilisateur(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurInexistantException(
                        "Aucun utilisateur trouve avec l'id " + id));
    }

    private void verifierEmailDisponible(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire.");
        }
        Optional<Utilisateur> existant = utilisateurRepository.findByEmail(email);
        if (existant.isPresent()) {
            throw new EmailDejaUtiliseException(
                    "Un compte existe deja avec l'email : " + email);
        }
    }

    private void verifierTelephoneDisponible(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            return;
        }
        Optional<Utilisateur> existant = utilisateurRepository.findByTelephone(telephone);
        if (existant.isPresent()) {
            throw new TelephoneDejaUtiliseException(
                    "Un compte existe deja avec le telephone : " + telephone);
        }
    }
}
