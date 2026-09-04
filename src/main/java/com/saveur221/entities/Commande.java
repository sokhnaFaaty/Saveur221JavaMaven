package com.saveur221.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.saveur221.enums.Statut;
import com.saveur221.exceptions.TransitionStatutInvalideException;

public class Commande {
    private Long id;
    private String numCommande;
    private LocalDateTime dateCommande;
    private double total;
    private Statut statut;
    private List<LigneCommande> lignes;

    public Commande() {
        this.lignes = new ArrayList<>();
    }

    public Commande(Long id, String numCommande, LocalDateTime dateCommande, double total,
            Statut statut) {
        this.id = id;
        this.numCommande = numCommande;
        this.dateCommande = dateCommande;
        this.total = total;
        this.statut = statut;
        this.lignes = new ArrayList<>();
    }

    public void ajouterLigne(LigneCommande ligne) {
        lignes.add(ligne);
        recalculerTotal();
    }

    private void recalculerTotal() {
        this.total = lignes.stream().mapToDouble(LigneCommande::getSousTotal).sum();
    }

    public void changerStatut(Statut nouveauStatut) {
        if (!this.statut.peutTransitionnerVers(nouveauStatut)) {
            throw new TransitionStatutInvalideException(
                    "Transition invalide : " + this.statut + " -> " + nouveauStatut);
        }
        this.statut = nouveauStatut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumCommande() {
        return numCommande;
    }

    public void setNumCommande(String numCommande) {
        this.numCommande = numCommande;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommande> lignes) {
        this.lignes = lignes;
    }

    @Override
    public String toString() {
        return "Commande{id=%d, numCommande='%s', total=%.0f, statut=%s}"
                .formatted(id, numCommande, total, statut);
    }

}
