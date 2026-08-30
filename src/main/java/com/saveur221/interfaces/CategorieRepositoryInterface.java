package com.saveur221.interfaces;

import java.util.List;
import java.util.Optional;

import com.saveur221.entities.Categorie;

public interface CategorieRepositoryInterface {
    Optional<Categorie> findById(Long id);
    List<Categorie> findAll();
    List<Categorie> search(String motCle);
    boolean contientDesProduits(Long categorieId);
    Categorie save(Categorie categorie);
    Categorie update(Categorie categorie);
    void delete(Long id);
}

