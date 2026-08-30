package com.saveur221.services;

import java.util.List;

import com.saveur221.entities.Categorie;
import com.saveur221.entities.Produit;
import com.saveur221.exceptions.ProduitInexistantException;
import com.saveur221.interfaces.CategorieRepositoryInterface;
import com.saveur221.exceptions.CategorieInexistanteException;
import com.saveur221.interfaces.ProduitRepositoryInterface;

public class ProduitService {
    private final ProduitRepositoryInterface produitRepository;
    private final CategorieRepositoryInterface categorieRepository;

    public ProduitService(ProduitRepositoryInterface produitRepository,
                           CategorieRepositoryInterface categorieRepository) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
    }

    public Produit ajouterProduit(String libelle, String description, double prix,
                                   int quantiteStock, Long categorieId, String image, int seuilAlerte) {
        if (libelle == null || libelle.isBlank()) {
            throw new IllegalArgumentException("Le libelle du produit est obligatoire.");
        }
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas etre negatif.");
        }
        if (quantiteStock < 0) {
            throw new IllegalArgumentException("La quantite en stock ne peut pas etre negative.");
        }

        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new CategorieInexistanteException(
                        "Aucune categorie trouvee avec l'id " + categorieId));

        Produit produit = new Produit(null, libelle, description, prix, quantiteStock,
                categorie, image, seuilAlerte);

        return produitRepository.save(produit);
    }

    public List<Produit> listerProduits() {
        return produitRepository.findAll();
    }

    public List<Produit> listerParCategorie(Long categorieId) {
        return produitRepository.findByCategorie(categorieId);
    }

    public List<Produit> rechercherProduit(String motCle) {
        return produitRepository.search(motCle);
    }

    public List<Produit> listerProduitsEnRupture() {
        return produitRepository.findEnRupture();
    }

    public List<Produit> listerProduitsStockFaible() {
        return produitRepository.findStockFaible();
    }

    public Produit modifierProduit(Long id, String libelle, String description, double prix,
                                    Long categorieId, String image, int seuilAlerte) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitInexistantException(
                        "Aucun produit trouve avec l'id " + id));

        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new CategorieInexistanteException(
                        "Aucune categorie trouvee avec l'id " + categorieId));

        produit.setLibelle(libelle);
        produit.setDescription(description);
        produit.setPrix(prix);
        produit.setCategorie(categorie);
        produit.setImage(image);
        produit.setSeuilAlerte(seuilAlerte);

        return produitRepository.update(produit);
    }

    public void supprimerProduit(Long id) {
        produitRepository.delete(id);
    }

    public Produit approvisionner(Long id, int quantite) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitInexistantException(
                        "Aucun produit trouve avec l'id " + id));

        produit.approvisionner(quantite);
        return produitRepository.update(produit);
    }

    // Utilise lors de la creation d'une commande (diminue le stock ligne par ligne)
    public Produit diminuerStock(Long id, int quantite) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitInexistantException(
                        "Aucun produit trouve avec l'id " + id));

        produit.diminuerStock(quantite);
        return produitRepository.update(produit);
    }

    // Utilise lors de l'annulation d'une commande (restitue le stock)
    public Produit restaurerStock(Long id, int quantite) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitInexistantException(
                        "Aucun produit trouve avec l'id " + id));

        produit.restaurerStock(quantite);
        return produitRepository.update(produit);
    }

    public Produit definirSeuilAlerte(Long id, int seuil) {
        if (seuil < 0) {
            throw new IllegalArgumentException("Le seuil d'alerte ne peut pas etre negatif.");
        }

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitInexistantException(
                        "Aucun produit trouve avec l'id " + id));

        produit.setSeuilAlerte(seuil);
        return produitRepository.update(produit);
    }

}
