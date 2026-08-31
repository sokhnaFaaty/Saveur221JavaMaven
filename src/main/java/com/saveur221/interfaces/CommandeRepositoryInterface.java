package com.saveur221.interfaces;

import java.util.List;
import java.util.Optional;

import com.saveur221.entities.Commande;
import com.saveur221.enums.Statut;

public interface CommandeRepositoryInterface {
    Optional<Commande> findById(Long id);
    List<Commande> findAll();
    List<Commande> findByStatut(Statut statut);
    List<Commande> findByClient(Long clientId);
    Commande save(Commande commande);
    Commande update(Commande commande);

}
