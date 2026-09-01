package com.saveur221.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.saveur221.entities.Commande;
import com.saveur221.entities.LigneCommande;
import com.saveur221.entities.Produit;
import com.saveur221.enums.Statut;
import com.saveur221.interfaces.CommandeRepositoryInterface;

public class StatistiqueService {
    private final CommandeRepositoryInterface commandeRepository;

    public StatistiqueService(CommandeRepositoryInterface commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    public double chiffreAffairesDuJour() {
        return chiffreAffairesEntre(LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay());
    }

    public double chiffreAffairesDeLaSemaine() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate debut = aujourdhui.minusDays(aujourdhui.getDayOfWeek().getValue() - 1L);
        return chiffreAffairesEntre(debut.atStartOfDay(), debut.plusDays(7).atStartOfDay());
    }

    public double chiffreAffairesDuMois() {
        LocalDate debut = LocalDate.now().withDayOfMonth(1);
        return chiffreAffairesEntre(debut.atStartOfDay(), debut.plusMonths(1).atStartOfDay());
    }

    private double chiffreAffairesEntre(LocalDateTime debut, LocalDateTime fin) {
        return toutesCommandesValides().stream()
                .filter(c -> !c.getDateCommande().isBefore(debut) && c.getDateCommande().isBefore(fin))
                .mapToDouble(Commande::getTotal)
                .sum();
    }

    public long nombreCommandes() {
        return commandeRepository.findAll().size();
    }

    public long nombreCommandesEnCours() {
        return commandeRepository.findAll().stream()
                .filter(c -> c.getStatut() == Statut.EN_ATTENTE
                        || c.getStatut() == Statut.EN_PREPARATION
                        || c.getStatut() == Statut.PRETE)
                .count();
    }

    public Produit produitLePlusVendu() {
        return topProduitsAvecQuantite().keySet().stream().findFirst().orElse(null);
    }

    public List<Produit> top3Produits() {
        return List.copyOf(topProduitsAvecQuantite().keySet());
    }

    public Map<Produit, Integer> topProduitsAvecQuantite() {
        Map<Long, Integer> quantites = new HashMap<>();
        Map<Long, Produit> produits = new HashMap<>();

        for (Commande commande : toutesCommandesValides()) {
            for (LigneCommande ligne : commande.getLignes()) {
                Produit produit = ligne.getProduit();
                if (produit == null) {
                    continue;
                }
                quantites.merge(produit.getId(), ligne.getQuantite(), Integer::sum);
                produits.putIfAbsent(produit.getId(), produit);
            }
        }

        return quantites.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(
                        e -> produits.get(e.getKey()),
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    private List<Commande> toutesCommandesValides() {
        return commandeRepository.findAll().stream()
                .filter(c -> c.getStatut() != Statut.ANNULEE)
                .collect(Collectors.toList());
    }
}
