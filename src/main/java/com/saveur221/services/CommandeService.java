package com.saveur221.services;

import java.util.List;

import com.saveur221.entities.Commande;
import com.saveur221.entities.LigneCommande;
import com.saveur221.enums.Statut;
import com.saveur221.exceptions.CommandeInexistanteException;
import com.saveur221.interfaces.CommandeRepositoryInterface;

public class CommandeService {
    private final CommandeRepositoryInterface commandeRepository;
    private final ProduitService produitService;

    public CommandeService(CommandeRepositoryInterface commandeRepository,
                            ProduitService produitService) {
        this.commandeRepository = commandeRepository;
        this.produitService = produitService;
    }

    public List<Commande> listerCommandes() {
        return commandeRepository.findAll();
    }

    public List<Commande> listerParStatut(Statut statut) {
        return commandeRepository.findByStatut(statut);
    }

    public List<Commande> listerParClient(Long clientId) {
        return commandeRepository.findByClient(clientId);
    }

    public Commande consulterCommande(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new CommandeInexistanteException(
                        "Aucune commande trouvee avec l'id " + id));
    }

    // Gere aussi bien les transitions normales que l'annulation
    // (qui restaure le stock de chaque ligne).
    public Commande changerStatut(Long commandeId, Statut nouveauStatut) {
        Commande commande = consulterCommande(commandeId);
        commande.changerStatut(nouveauStatut);

        if (nouveauStatut == Statut.ANNULEE) {
            for (LigneCommande ligne : commande.getLignes()) {
                produitService.restaurerStock(ligne.getProduit().getId(), ligne.getQuantite());
            }
        }

        return commandeRepository.update(commande);
    }

    public Commande annulerCommande(Long commandeId) {
        return changerStatut(commandeId, Statut.ANNULEE);
    }
}