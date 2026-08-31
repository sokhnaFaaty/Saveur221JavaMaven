
-- ============================================================
-- SAVEUR221 - Script de creation de la base de donnees
-- Base partagee entre l'application Java (console, staff)
-- et l'application PHP (web, clients)
-- ============================================================



-- ============================================================
-- 1. UTILISATEURS (personnel interne : ADMIN, GERANT)
-- ============================================================
CREATE TABLE utilisateurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    telephone VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'GERANT')),
    actif BOOLEAN NOT NULL DEFAULT true,
    image VARCHAR(255),
    deleted_at TIMESTAMP NULL
);

-- ============================================================
-- 2. CLIENTS (utilisateurs cote PHP uniquement)
-- ============================================================
CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    deleted_at TIMESTAMP NULL
);

-- ============================================================
-- 3. CATEGORIES
-- ============================================================
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    description TEXT,
    deleted_at TIMESTAMP NULL
);

-- ============================================================
-- 4. PRODUITS
-- (pas de colonne "disponible" : elle est calculee en Java a
--  partir de quantite_stock, jamais stockee en base)
-- ============================================================
CREATE TABLE produits (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(150) NOT NULL,
    description TEXT,
    prix NUMERIC(10,2) NOT NULL,
    quantite_stock INTEGER NOT NULL DEFAULT 0,
    categorie_id INTEGER NOT NULL REFERENCES categories(id),
    image VARCHAR(255),
    seuil_alerte INTEGER NOT NULL DEFAULT 5,
    deleted_at TIMESTAMP NULL
);

-- ============================================================
-- 5. COMMANDES
-- (pas de soft delete : une commande n'est jamais supprimee,
--  elle passe au statut ANNULEE)
-- ============================================================
CREATE TABLE commandes (
    id SERIAL PRIMARY KEY,
    num_commande VARCHAR(50) NOT NULL UNIQUE,
    date_commande TIMESTAMP NOT NULL DEFAULT NOW(),
    total NUMERIC(10,2) NOT NULL DEFAULT 0,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE'
        CHECK (statut IN ('EN_ATTENTE','EN_PREPARATION','PRETE','RETIREE','ANNULEE')),
    client_id INTEGER NOT NULL REFERENCES clients(id)
);

-- ============================================================
-- 6. LIGNES DE COMMANDE
-- ============================================================
CREATE TABLE ligne_commandes (
    id SERIAL PRIMARY KEY,
    quantite INTEGER NOT NULL,
    prix_unitaire NUMERIC(10,2) NOT NULL,
    sous_total NUMERIC(10,2) NOT NULL,
    produit_id INTEGER NOT NULL REFERENCES produits(id),
    commande_id INTEGER NOT NULL REFERENCES commandes(id)
);

-- ============================================================
-- 7. PAIEMENTS
-- ============================================================
CREATE TABLE paiements (
    id SERIAL PRIMARY KEY,
    montant NUMERIC(10,2) NOT NULL,
    date_paiement TIMESTAMP NOT NULL DEFAULT NOW(),
    commande_id INTEGER NOT NULL REFERENCES commandes(id)
);

-- ============================================================
-- 8. FACTURES
-- ============================================================
CREATE TABLE factures (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    date_emission TIMESTAMP NOT NULL DEFAULT NOW(),
    montant_total NUMERIC(10,2) NOT NULL,
    commande_id INTEGER NOT NULL REFERENCES commandes(id)
);

-- ============================================================
-- 9. RECUS
-- ============================================================
CREATE TABLE recus (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    date_emission TIMESTAMP NOT NULL DEFAULT NOW(),
    montant NUMERIC(10,2) NOT NULL,
    paiement_id INTEGER NOT NULL REFERENCES paiements(id)
);

-- ============================================================
-- 10. AVIS (cote PHP uniquement, un avis par commande)
-- ============================================================
CREATE TABLE avis (
    id SERIAL PRIMARY KEY,
    note INTEGER NOT NULL CHECK (note BETWEEN 1 AND 5),
    commentaire TEXT,
    date_avis TIMESTAMP NOT NULL DEFAULT NOW(),
    client_id INTEGER NOT NULL REFERENCES clients(id),
    commande_id INTEGER NOT NULL UNIQUE REFERENCES commandes(id),
    deleted_at TIMESTAMP NULL
);

-- ============================================================
-- DONNEES DE TEST (2 utilisateurs pour tester la connexion Java)
-- Mots de passe en clair : admin123 / gerant123 (deja hashes en SHA-256)
-- ============================================================
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, telephone, role, actif, image)
VALUES
('Diop', 'Amadou', 'admin@saveur221.sn', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '771234567', 'ADMIN', true, NULL),
('Fall', 'Ibrahima', 'gerant@saveur221.sn', '0adea017a51a0224047865ad5b90b53289a93f01ef1b798ef8ae079b3c161640', '9012345', 'GERANT', true, NULL);