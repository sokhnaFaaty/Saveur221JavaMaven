package com.saveur221.entities;

import com.saveur221.enums.Etat;
import com.saveur221.exceptions.StockInsuffisantException;

public class Produit {
    private Long id;
    private String libelle;
    private String description;
    private double prix;
    private int quantiteStock;
    private Categorie categorie;
    private Etat disponible;
    private String image;
    private int seuilAlerte;

    public Produit(){

    }

    public Produit(Long id, String libelle, String description, double prix, int quantiteStock,
        Categorie categorie, String image, int seuilAlerte){
            this.id = id;
            this.libelle = libelle;
            this.description = description;
            this.prix = prix;
            this.quantiteStock = quantiteStock;
            this.categorie = categorie;
            this.image = image;
            this.seuilAlerte = seuilAlerte;
            ajusterDisponibilite();
        }

    public void approvisionner(int quantite){
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive.");
        }
        this.quantiteStock += quantite;
        ajusterDisponibilite();
    }

    public void diminuerStock(int quantite){
        if (quantite > this.quantiteStock) {
            throw new StockInsuffisantException(
                "Stock insuffisant pour '" + libelle + "' : demande=" + quantite + ", disponible=" + quantiteStock);
        }
        this.quantiteStock -= quantite;
        ajusterDisponibilite();
    }

    public void restaurerStock(int quantite){
        this.quantiteStock += quantite;
        ajusterDisponibilite();
    }

    private void ajusterDisponibilite(){
        this.disponible = (quantiteStock <= 0) ? Etat.NON_DISPONIBLE : Etat.DISPONIBLE;
    }

    public boolean estEnRupture(){
        return quantiteStock <= 0;
    }

    public boolean stockFaible(){
        return quantiteStock > 0 && quantiteStock <= seuilAlerte;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getLibelle(){
        return libelle;
    }

    public void setLibelle(String libelle){
        this.libelle = libelle;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public double getPrix(){
        return prix;
    }

    public void setPrix(double prix){
        this.prix = prix;
    }

    public int getQuantiteStock(){
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock){
        this.quantiteStock = quantiteStock;
        ajusterDisponibilite();
    }

    public Categorie getCategorie(){
        return categorie;
    }

    public void setCategorie(Categorie categorie){
        this.categorie = categorie;
    }

    public Etat getDisponible(){
        return disponible;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public int getSeuilAlerte(){
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte){
        this.seuilAlerte = seuilAlerte;
    }

    @Override
    public String toString() {
        return "Produit{id=%d, libelle='%s', prix=%.0f, stock=%d, etat=%s}"
                .formatted(id, libelle, prix, quantiteStock, disponible);
    }
}