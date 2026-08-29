package com.saveur221.entities;

import com.saveur221.enums.Role;
public class Utilisateur {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private Role role;
    private boolean actif;
    private String image;

    public Utilisateur(){

    }

    public Utilisateur(Long id, String nom, String prenom, String email, String motDePasse,
        String telephone, Role role, boolean actif, String image){
            this.id=id;
            this.nom=nom;
            this.prenom=prenom;
            this.motDePasse=motDePasse;
            this.telephone=telephone;
            this.role=role;
            this.actif=actif;
            this.image=image;
        }
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNom(){
        return nom;
    }

    public void setNom(String nom){
        this.nom=nom;
    }

    public String getPrenom(String prenom){
        return prenom;
    }

    public void setPrenom(String prenom){
        this.prenom = prenom;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getMotDePasse(){
        return motDePasse;
    }
    public void setMotDePasse(String motDePasse){
        this.motDePasse= motDePasse;
    }

    public String getTelephone(){
        return telephone;
    }
    
    public void setTelephone(String telephone){
        this.telephone=telephone;
    }
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Utilisateur{id=%d, nom='%s %s', email='%s', role=%s, actif=%b}"
                .formatted(id, prenom, nom, email, role, actif);
    }
}
