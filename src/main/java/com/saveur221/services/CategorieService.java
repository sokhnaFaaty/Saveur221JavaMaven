package com.saveur221.services;

import java.util.List;

import com.saveur221.entities.Categorie;
import com.saveur221.exceptions.CategorieInexistanteException;
import com.saveur221.exceptions.LibelleDejaUtiliseException;
import com.saveur221.interfaces.CategorieRepositoryInterface;


public class CategorieService {
    private final CategorieRepositoryInterface categorieRepository;

    public CategorieService(CategorieRepositoryInterface categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    public Categorie ajouterCategorie(String libelle, String description) {
        if (libelle == null || libelle.isBlank()) {
            throw new IllegalArgumentException("Le libelle de la categorie est obligatoire.");
        }
        if (categorieRepository.findByLibelle(libelle).isPresent()) {
            throw new LibelleDejaUtiliseException("Une categorie avec ce libelle existe deja.");
        }
        Categorie categorie = new Categorie(null, libelle, description);
        return categorieRepository.save(categorie);
    }

    public List<Categorie> listerCategories() {
        return categorieRepository.findAll();
    }

    public List<Categorie> rechercherCategorie(String motCle) {
        return categorieRepository.search(motCle);
    }

    public Categorie modifierCategorie(Long id, String libelle, String description) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new CategorieInexistanteException(
                        "Aucune categorie trouvee avec l'id " + id));

        if (categorieRepository.findByLibelle(libelle)
                .filter(c -> !c.getId().equals(id))
                .isPresent()) {
            throw new IllegalArgumentException("Une categorie avec ce libelle existe deja.");
        }

        categorie.setLibelle(libelle);
        categorie.setDescription(description);

        return categorieRepository.update(categorie);
    }

    public void supprimerCategorie(Long id) {
        // La verification "contient des produits ?" est deja faite
        // dans CategorieRepositoryImpl.delete() -> pas besoin de la refaire ici.
        categorieRepository.delete(id);
    }

    
}
