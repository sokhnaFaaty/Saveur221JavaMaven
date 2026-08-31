package com.saveur221.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.saveur221.entities.Client;
import com.saveur221.entities.Commande;
import com.saveur221.entities.LigneCommande;
import com.saveur221.entities.Produit;
import com.saveur221.enums.Statut;
import com.saveur221.exceptions.CommandeInexistanteException;
import com.saveur221.interfaces.ClientRepositoryInterface;
import com.saveur221.interfaces.CommandeRepositoryInterface;
import com.saveur221.interfaces.ProduitRepositoryInterface;

public class CommandeService {
    private final CommandeRepositoryInterface commandeRepository;
    private final ClientRepositoryInterface clientRepository;
    private final ProduitRepositoryInterface produitRepository;
    private final ProduitService produitService;

    public CommandeService(CommandeRepositoryInterface commandeRepository,
                            ClientRepositoryInterface clientRepository,
                            ProduitRepositoryInterface produitRepository,
                            ProduitService produitService) {
        this.commandeRepository = commandeRepository;
        this.clientRepository = clientRepository;
        this.produitRepository = produitRepository;
        this.produitService = produitService;
    }

    // lignesDemandees : cle = id du produit, valeur = quantite souhaitee
    public Commande passerCommande(Long clientId, Map<Long, Integer> lignesDemandees,
                                    Map<Long, String> instructionsParProduit) {

        if (lignesDemandees == null || lignesDemandees.isEmpty()) {
            throw new IllegalArgumentException("Une commande doit contenir au moins un article.");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientInexistantException(
                        "Aucun client trouve avec l'id " + clientId));

        // On verifie et prepare toutes les lignes AVANT de toucher au stock,
        // pour ne rien diminuer si une seule ligne est invalide.
        List<LigneCommande> lignes = new ArrayList<>();
        for (Map.Entry<Long, Integer> entree : lignesDemandees.entrySet()) {
            Long produitId = entree.getKey();
            int quantite = entree.getValue();

            if (quantite <= 0) {
                throw new IllegalArgumentException("La quantite doit etre positive.");
            }

            Produit produit = produitRepository.findById(produitId)
                    .orElseThrow(() -> new com.saveur221.exceptions.ProduitInexistantException(
                            "Aucun produit trouve avec l'id " + produitId));

            // diminuerStock() de l'entite Produit leve StockInsuffisantException
            // si la quantite demandee depasse le stock disponible.
            produit.diminuerStock(quantite);

            String instructions = instructionsParProduit != null
                    ? instructionsParProduit.get(produitId)
                    : null;

            lignes.add(new LigneCommande(null, quantite, produit.getPrix(), produit,
                    null, instructions));
        }

        // Une fois toutes les lignes validees, on persiste la diminution de stock.
        for (LigneCommande ligne : lignes) {
            produitService.diminuerStock(ligne.getProduit().getId(), ligne.getQuantite());
        }

        Commande commande = new Commande(null, genererNumCommande(), LocalDateTime.now(),
                0, Statut.EN_ATTENTE, client);
        lignes.forEach(commande::ajouterLigne);

        return commandeRepository.save(commande);
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

        // La verification de transition valide est faite dans Commande.changerStatut()
        // (leve TransitionStatutInvalideException si besoin).
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

    
    private String genererNumCommande() {
        int annee = LocalDateTime.now().getYear();
        long suffixe = System.currentTimeMillis() % 100000;
        return "CMD-%d-%05d".formatted(annee, suffixe);
    }

}
