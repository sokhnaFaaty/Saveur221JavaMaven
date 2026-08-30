package com.saveur221.entities;

public class Categorie {
    private Long id;
    private String libelle;
    private String description;

    public Categorie(){

    }

    public Categorie(Long id, String libelle, String description){
        this.id = id;
        this.libelle = libelle;
        this.description = description;
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

    @Override
    public String toString() {
        return "Categorie{id=%d, libelle='%s'}".formatted(id, libelle);
    }

}
