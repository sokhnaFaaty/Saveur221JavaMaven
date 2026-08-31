package com.saveur221.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.saveur221.entities.Commande;
import com.saveur221.entities.Paiement;
import com.saveur221.enums.Etat;
import com.saveur221.exceptions.MontantPaiementInvalideException;
import com.saveur221.interfaces.PaiementRepositoryInterface;

public class PaiementService {
    private final PaiementRepositoryInterface paiementRepository;
    private final CommandeService commandeService;

    public PaiementService(PaiementRepositoryInterface paiementRepository,
                            CommandeService commandeService) {
        this.paiementRepository = paiementRepository;
        this.commandeService = commandeService;
    }

    public Paiement enregistrerPaiement(Long commandeId, double montant) {
        if (montant <= 0) {
            throw new MontantPaiementInvalideException("Le montant doit etre positif.");
        }

        Commande commande = commandeService.consulterCommande(commandeId);
        double dejaPaye = paiementRepository.sommePaiements(commandeId);
        double montantRestant = commande.getTotal() - dejaPaye;

        if (montant > montantRestant) {
            throw new MontantPaiementInvalideException(
                    "Le montant depasse le reste a payer (%.0f restant)".formatted(montantRestant));
        }

        Paiement paiement = new Paiement(null, montant, LocalDateTime.now(), commandeId);
        return paiementRepository.save(paiement);
    }

    public List<Paiement> listerPaiementsCommande(Long commandeId) {
        return paiementRepository.findByCommande(commandeId);
    }

    public double montantRestant(Long commandeId) {
        Commande commande = commandeService.consulterCommande(commandeId);
        double dejaPaye = paiementRepository.sommePaiements(commandeId);
        return commande.getTotal() - dejaPaye;
    }

    public StatutPaiement calculerStatutPaiement(Long commandeId) {
        Commande commande = commandeService.consulterCommande(commandeId);
        double dejaPaye = paiementRepository.sommePaiements(commandeId);

        if (dejaPaye <= 0) {
            return StatutPaiement.IMPAYEE;
        }
        if (dejaPaye < commande.getTotal()) {
            return StatutPaiement.PARTIELLEMENT_PAYEE;
        }
        return StatutPaiement.PAYEE;
    }

    public List<Commande> listerCommandesImpayees() {
        return commandeService.listerCommandes().stream()
                .filter(c -> calculerStatutPaiement(c.getId()) == StatutPaiement.IMPAYEE)
                .collect(Collectors.toList());
    }

    public List<Commande> listerCommandesPartiellementPayees() {
        return commandeService.listerCommandes().stream()
                .filter(c -> calculerStatutPaiement(c.getId()) == StatutPaiement.PARTIELLEMENT_PAYEE)
                .collect(Collectors.toList());
    }

}
