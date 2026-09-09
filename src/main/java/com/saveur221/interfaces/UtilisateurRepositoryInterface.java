package com.saveur221.interfaces;

import java.util.List;
import java.util.Optional;

import com.saveur221.entities.Utilisateur;

public interface UtilisateurRepositoryInterface {
    // Récupérer un utilisateur par son ID
    Optional<Utilisateur> findById(Long id);
    
    // Trouver un utilisateur avec son email
    Optional<Utilisateur> findByEmail(String email);

    // Trouver un utilisateur avec son telephone
    Optional<Utilisateur> findByTelephone(String telephone);
    
    // Lister tous les utilisateurs
    List<Utilisateur> findAll();
    
    // Rechercher des utilisateurs par mot-clé
    List<Utilisateur> search(String motCle);
    
    // Ajouter un nouvel utilisateur
    Utilisateur save(Utilisateur utilisateur);
    
    // Modifier les infos d'un utilisateur
    Utilisateur update(Utilisateur utilisateur);

    // Supprimer un utilisateur (soft delete)
    void delete(Long id);

    // Lister les utilisateurs supprimes (corbeille)
    List<Utilisateur> findAllDeleted();

    // Trouver un utilisateur supprime par son ID (corbeille)
    Optional<Utilisateur> findDeletedById(Long id);

    // Restaurer un utilisateur supprime
    void restaurer(Long id);

    // Purger definitivement un utilisateur (suppression reelle)
    void purger(Long id);
}
