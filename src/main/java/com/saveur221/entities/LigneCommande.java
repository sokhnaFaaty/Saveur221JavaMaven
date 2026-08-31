package com.saveur221.entities;

public class LigneCommande {
    private Long id;
    private int quantite;
    private double prixUnitaire;
    private double sousTotal;
    private Produit produit;
    private Long commandeId;
    private String instructionsSpeciales;

    public LigneCommande(){

    }

    public LigneCommande(Long id, int quantite, double prixUnitaire, Produit produit,
        Long commandeId, String instructionsSpeciales){
            this.id = id;
            this.quantite = quantite;
            this.prixUnitaire = prixUnitaire;
            this.produit = produit;
            this.commandeId = commandeId;
            this.instructionsSpeciales = instructionsSpeciales;
            this.sousTotal = quantite * prixUnitaire;
        }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public int getQuantite(){
        return quantite;
    }

    public void setQuantite(int quantite){
        this.quantite = quantite;
        this.sousTotal = quantite * prixUnitaire;
    }

    public double getPrixUnitaire(){
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire){
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = quantite * prixUnitaire;
    }

    public double getSousTotal(){
        return sousTotal;
    }

    public Produit getProduit(){
        return produit;
    }

    public void setProduit(Produit produit){
        this.produit = produit;
    }

    public Long getCommandeId(){
        return commandeId;
    }

    public void setCommandeId(Long commandeId){
        this.commandeId = commandeId;
    }

    public String getInstructionsSpeciales(){
        return instructionsSpeciales;
    }

    public void setInstructionsSpeciales(String instructionsSpeciales){
        this.instructionsSpeciales = instructionsSpeciales;
    }

    @Override
    public String toString() {
        return "LigneCommande{produit='%s', quantite=%d, sousTotal=%.0f}"
                .formatted(produit != null ? produit.getLibelle() : "?", quantite, sousTotal);
    }

}
