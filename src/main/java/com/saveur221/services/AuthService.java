package com.saveur221.services;

import java.util.Optional;
import java.util.regex.Pattern;

import com.saveur221.entities.Utilisateur;
import com.saveur221.enums.Role;
import com.saveur221.exceptions.CompteDesactiveException;
import com.saveur221.exceptions.MotDePasseIncorrectException;
import com.saveur221.exceptions.UtilisateurInexistantException;
import com.saveur221.interfaces.UtilisateurRepositoryInterface;

public class AuthService {
    private final UtilisateurRepositoryInterface utilisateurRepository;

    public AuthService(UtilisateurRepositoryInterface utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Utilisateur authentifier(String email, String motDePasseSaisi) {
        Optional<Utilisateur> resultat = utilisateurRepository.findByEmail(email);

        Utilisateur utilisateur = resultat.orElseThrow(() ->
                new UtilisateurInexistantException("Aucun compte associe a l'email : " + email));

        if (!verifierMotDePasse(motDePasseSaisi, utilisateur.getMotDePasse())) {
            throw new MotDePasseIncorrectException("Mot de passe incorrect.");
        }

        if (!utilisateur.isActif()) {
            throw new CompteDesactiveException(
                    "Ce compte a ete desactive. Contactez un administrateur.");
        }

        return utilisateur;
    }

    public boolean verifierMotDePasse(String saisi, String hash) {
        return org.mindrot.jbcrypt.BCrypt.checkpw(saisi, hash);
    }

    public String hasherMotDePasse(String motDePasseClair) {
        return org.mindrot.jbcrypt.BCrypt.hashpw(motDePasseClair, org.mindrot.jbcrypt.BCrypt.gensalt());
    }

    private static final Pattern LONGUEUR_MIN = Pattern.compile("^.{6,}$");

    public boolean motDePasseValide(String motDePasseClair) {
        return motDePasseClair != null && LONGUEUR_MIN.matcher(motDePasseClair).matches();
    }

    public boolean estAdmin(Utilisateur utilisateur) {
        return utilisateur.getRole() == Role.ADMIN;
    }

    public boolean estGerant(Utilisateur utilisateur) {
        return utilisateur.getRole() == Role.GERANT;
    }

}
