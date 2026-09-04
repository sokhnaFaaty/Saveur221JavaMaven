# SAVEUR221 - Application Java Console

Application **Java en console** pour la gestion interne du restaurant **Saveur221**.

Elle est destinée au personnel interne (gérant et administrateur uniquement), en complément de la partie PHP du site. Cette application Java ne gère **pas** les clients — seulement les utilisateurs internes, les produits, le stock, les commandes, les paiements et les statistiques.

---

## Fonctionnalités

### Connexion
- Connexion par **email** et **mot de passe**
- Vérification que le compte existe, que le mot de passe est correct, que le compte est **actif**
- L'**admin** et le **gérant** ont des menus différents

### Gérant

**Catégories**
- Ajouter, afficher, rechercher, modifier, supprimer une catégorie
- Une catégorie contenant des produits **ne peut pas** être supprimée

**Produits**
- Ajouter, afficher, modifier, supprimer, rechercher un produit
- Filtrer par catégorie
- Voir les produits **disponibles** / **indisponibles**
- Attributs : libellé, description, prix, quantité en stock, catégorie, disponible, image

**Stock**
- Consulter le stock, approvisionner un produit, augmenter la quantité
- Définir un seuil de stock
- Voir les produits en **stock faible** et en **rupture**

**Commandes**
- Afficher les commandes, consulter une commande
- Changer le statut, annuler une commande
- Statuts : `EN_ATTENTE`, `EN_PREPARATION`, `PRETE`, `RETIREE`, `ANNULEE`
- L'annulation d'une commande **restaure le stock**

**Paiements**
- Afficher les paiements, voir les commandes impayées / partiellement payées
- Enregistrer un paiement (ne dépasse jamais le montant restant)
- **Historique complet** de tous les paiements

**Statistiques**
- Chiffre d'affaires du jour, de la semaine et du mois
- Nombre de commandes
- Commandes en cours
- Produit le plus vendu
- Top 3 des produits
- Nombre de commandes par statut

### Administrateur
L'admin peut **tout faire comme le gérant**, plus la **gestion des utilisateurs internes** :
- Ajouter, afficher, rechercher, modifier, supprimer un utilisateur
- Activer / désactiver un compte
- Changer le rôle

Rôles : `ADMIN`, `GERANT`

---

## Technologies

- **Java 17**
- **Maven**
- **PostgreSQL** + JDBC

---

## Prérequis

- JDK 17+
- Maven 3.x
- PostgreSQL en cours d'exécution

---

## Configuration de la base de données

Crée une base PostgreSQL puis renseigne les informations de connexion dans le fichier :

```
src/main/resources/application.properties
```

Exemple :

```properties
db.url=jdbc:postgresql://localhost:5432/saveur221
db.user=postgres
db.password=ton_mot_de_passe
```

Le schéma de la base est fourni dans `src/main/java/com/saveur221/script.sql`.

---

## Compilation et exécution

Depuis la racine du projet :

```bash
# Compiler
mvn compile

# Lancer l'application
mvn compile exec:java

# Ou générer un jar exécutable (classe principale: com.saveur221.Main)
mvn package
java -jar target/saveur221-java.jar
```

---

## Structure du projet

```
src/main/java/com/saveur221/
├── Main.java                 # Point d'entrée
├── config/                   # Configuration (connexion base de données)
├── entities/                 # Entités (Utilisateur, Produit, Categorie, Commande, ...)
├── enums/                    # Énumérations (Role, Statut, Etat, StatutPaiement)
├── exceptions/               # Exceptions métier
├── interfaces/               # Interfaces des repositories
├── repository/               # Implémentations des repositories (JDBC)
├── services/                 # Logique métier (couche service)
└── views/                    # Menus et vues console
```

---

## Comptes

Les comptes administrateurs et gérants sont créés directement en base de données
(ou créés ensuite par un administrateur via l'application).
