package com.saveur221.entities;

import java.time.LocalDateTime;

public class Paiement {
    private Long id;
    private double montant;
    private LocalDateTime datePaiement;
    private Long commandeId;

    public Paiement() {
    }

    public Paiement(Long id, double montant, LocalDateTime datePaiement, Long commandeId) {
        this.id = id;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.commandeId = commandeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }

    @Override
    public String toString() {
        return "Paiement{id=%d, montant=%.0f, commandeId=%d}".formatted(id, montant, commandeId);
    }

}
