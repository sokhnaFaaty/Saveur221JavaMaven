package com.saveur221.interfaces;

import java.util.List;
import java.util.Optional;

import com.saveur221.entities.Produit;

public interface ProduitRepositoryInterface {
    Optional<Produit> findById(Long id);
    List<Produit> findAll();
    List<Produit> findByCategorie(Long categorieId);
    List<Produit> search(String motCle);
    List<Produit> findEnRupture();
    List<Produit> findStockFaible();
    Produit save(Produit produit);
    Produit update(Produit produit);
    void delete(Long id);
}