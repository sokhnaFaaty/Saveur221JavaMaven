package com.saveur221.interfaces;

import java.util.List;
import java.util.Optional;

import com.saveur221.entities.Paiement;

public interface PaiementRepositoryInterface {
    Optional<Paiement> findById(Long id);
    List<Paiement> findByCommande(Long commandeId);
    double sommePaiements(Long commandeId);
    Paiement save(Paiement paiement);

}
