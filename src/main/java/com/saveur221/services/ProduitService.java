package com.saveur221.services;

import java.util.ArrayList;
import java.util.List;

import com.saveur221.entities.Categorie;
import com.saveur221.entities.Produit;
import com.saveur221.enums.Etat;
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
                                   int quantiteStock, Long categorieId, int tempsPreparation, int calories, String image, int seuilAlerte) {
        if (libelle == null || libelle.isBlank()) {
            throw new IllegalArgumentException("Le libelle du produit est obligatoire.");
        }
        if (produitRepository.findByLibelle(libelle).isPresent()) {
            throw new IllegalArgumentException("Un produit avec ce libelle existe deja.");
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
                categorie, tempsPreparation, calories, image, seuilAlerte);

        return produitRepository.save(produit);
    }

    public List<Produit> listerProduits() {
        return produitRepository.findAll();
    }

    public Produit consulterProduit(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ProduitInexistantException(
                        "Aucun produit trouve avec l'id " + id));
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

    // Produits disponibles (stock > 0)
    public List<Produit> listerProduitsDisponibles() {
        List<Produit> resultat = new ArrayList<>();
        for (Produit p : listerProduits()) {
            if (p.getDisponible() == Etat.DISPONIBLE) {
                resultat.add(p);
            }
        }
        return resultat;
    }

    // Produits indisponibles (stock = 0)
    public List<Produit> listerProduitsIndisponibles() {
        List<Produit> resultat = new ArrayList<>();
        for (Produit p : listerProduits()) {
            if (p.getDisponible() == Etat.NON_DISPONIBLE) {
                resultat.add(p);
            }
        }
        return resultat;
    }

    public Produit modifierProduit(Long id, String libelle, String description, double prix,
                                    Long categorieId,int tempsPreparation, int calories, String image, int seuilAlerte) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitInexistantException(
                        "Aucun produit trouve avec l'id " + id));

        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new CategorieInexistanteException(
                        "Aucune categorie trouvee avec l'id " + categorieId));

        if (produitRepository.findByLibelle(libelle)
                .filter(p -> !p.getId().equals(id))
                .isPresent()) {
            throw new IllegalArgumentException("Un produit avec ce libelle existe deja.");
        }

        produit.setLibelle(libelle);
        produit.setDescription(description);
        produit.setPrix(prix);
        produit.setCategorie(categorie);
        produit.setTempsPreparation(tempsPreparation);
        produit.setCalories(calories);
        produit.setImage(image);
        produit.setSeuilAlerte(seuilAlerte);

        return produitRepository.update(produit);
    }

    public void supprimerProduit(Long id) {
        produitRepository.delete(id);
    }

    public List<Produit> listerProduitsSupprimees() {
        return produitRepository.findAllDeleted();
    }

    public Produit restaurerProduit(Long id) {
        Produit produit = produitRepository.findDeletedById(id)
                .orElseThrow(() -> new ProduitInexistantException(
                        "Aucun produit supprime trouve avec l'id " + id));

        if (produitRepository.findByLibelle(produit.getLibelle()).isPresent()) {
            throw new IllegalArgumentException("Impossible de restaurer : un produit avec ce libelle existe deja.");
        }

        // Si la categorie du produit est elle aussi supprimee, il faut
        // d'abord la restaurer pour garder des donnees coherentes.
        Categorie categorie = categorieRepository.findById(produit.getCategorie().getId())
                .orElseThrow(() -> new CategorieInexistanteException(
                        "Impossible de restaurer : la categorie '" + produit.getCategorie().getLibelle()
                        + "' est aussi dans la corbeille. Restaurez-la d'abord."));

        produit.setCategorie(categorie);
        produitRepository.restaurer(id);
        return produit;
    }

    public void purgerProduit(Long id) {
        if (produitRepository.findDeletedById(id).isEmpty()) {
            throw new ProduitInexistantException(
                    "Aucun produit supprime trouve avec l'id " + id);
        }
        produitRepository.purger(id);
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
