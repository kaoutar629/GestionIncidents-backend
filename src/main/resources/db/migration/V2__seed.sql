

USE gestion_incidents;

-- Vider dans l'ordre inverse des dépendances
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE logs;
TRUNCATE TABLE notifications;
TRUNCATE TABLE incidents;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
--  USERS
-- =============================================================================
INSERT INTO users (id, email, password, first_name, last_name, role, created_at) VALUES
-- password : Admin123!
(1, 'admin@demo.com',
 '$2a$10$Ek7N5pLHv5bAVrgfH0kSIuJqWEbFNEBqlAoiSJFJ6z6LVPWwkVzOq',
 'Kaoutar', 'Admin', 'ADMIN', NOW() - INTERVAL 60 DAY),

-- password : User123!
(2, 'alice@demo.com',
 '$2a$10$7cLBbLl1SZW4k.g2TaVEpuTI1h0I3KJDHGRt3kBk1YR57q8y7N7dW',
 'Alice', 'Martin', 'USER', NOW() - INTERVAL 45 DAY),

(3, 'bob@demo.com',
 '$2a$10$7cLBbLl1SZW4k.g2TaVEpuTI1h0I3KJDHGRt3kBk1YR57q8y7N7dW',
 'Bob', 'Dupont', 'USER', NOW() - INTERVAL 30 DAY),

(4, 'charlie@demo.com',
 '$2a$10$7cLBbLl1SZW4k.g2TaVEpuTI1h0I3KJDHGRt3kBk1YR57q8y7N7dW',
 'Charlie', 'Leblanc', 'USER', NOW() - INTERVAL 15 DAY);

-- =============================================================================
--  INCIDENTS  (répartis sur les 2 derniers mois pour alimenter le graphique)
-- =============================================================================
INSERT INTO incidents
(id, title, description, status, priority, category, created_by, created_at, updated_at, resolved_at)
VALUES

-- ── Incidents d'Alice ────────────────────────────────────────────────────────
(1,  'Imprimante bureau 3 hors service',
 'L\'imprimante HP LaserJet ne répond plus depuis ce matin. Le voyant clignote en rouge.',
     'RESOLVED', 'MEDIUM', 'Matériel',
     2, NOW() - INTERVAL 50 DAY, NOW() - INTERVAL 48 DAY, NOW() - INTERVAL 48 DAY),

(2,  'Accès VPN impossible depuis le télétravail',
     'Impossible de me connecter au VPN depuis chez moi. Erreur « Authentication failed ».',
     'CLOSED', 'HIGH', 'Accès',
     2, NOW() - INTERVAL 42 DAY, NOW() - INTERVAL 40 DAY, NOW() - INTERVAL 40 DAY),

(3,  'Écran de connexion Windows bloqué',
     'Mon poste affiche un écran bleu au démarrage avec le code STOP: 0x0000007B.',
     'IN_PROGRESS', 'HIGH', 'Logiciel',
     2, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 9 DAY, NULL),

(4,  'Lenteur sur l\'ERP ce matin',
 'L\'application RH est très lente, chaque requête prend plus de 30 secondes.',
     'OPEN', 'MEDIUM', 'Performance',
     2, NOW() - INTERVAL 2 DAY, NULL, NULL),

-- ── Incidents de Bob ─────────────────────────────────────────────────────────
(5,  'Antivirus expiré sur 5 postes',
     'Les 5 postes de la salle de réunion A affichent une alerte de licence expirée.',
     'RESOLVED', 'HIGH', 'Sécurité',
     3, NOW() - INTERVAL 38 DAY, NOW() - INTERVAL 36 DAY, NOW() - INTERVAL 36 DAY),

(6,  'Réseau Wi-Fi coupé en salle de conf',
     'Le Wi-Fi de la salle Panorama ne fonctionne plus depuis la mise à jour du routeur.',
     'RESOLVED', 'MEDIUM', 'Réseau',
     3, NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 24 DAY, NOW() - INTERVAL 23 DAY),

(7,  'Mise à jour Office bloque l\'ouverture des fichiers Excel',
 'Depuis la mise à jour d\'hier, Excel affiche « Fichier endommagé » pour tous les .xlsx.',
     'IN_PROGRESS', 'HIGH', 'Logiciel',
     3, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 4 DAY, NULL),

(8,  'Badge d\'accès refusé à l\'entrée',
     'Mon badge ne fonctionne plus pour entrer dans la salle serveur.',
     'OPEN', 'LOW', 'Accès',
     3, NOW() - INTERVAL 1 DAY, NULL, NULL),

-- ── Incidents de Charlie ─────────────────────────────────────────────────────
(9,  'Panne switch niveau 2 - bâtiment B',
     'Le switch du 2e étage du bâtiment B est tombé, 12 postes sans réseau.',
     'CLOSED', 'HIGH', 'Réseau',
     4, NOW() - INTERVAL 55 DAY, NOW() - INTERVAL 54 DAY, NOW() - INTERVAL 53 DAY),

(10, 'Sauvegarde automatique échoue depuis 3 jours',
     'Le job de sauvegarde nocturne renvoie une erreur « Disk quota exceeded ».',
     'RESOLVED', 'HIGH', 'Logiciel',
     4, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 18 DAY),

(11, 'Clé USB non reconnue sur poste RH',
     'Le poste de la responsable RH ne détecte aucune clé USB depuis la mise à jour BIOS.',
     'OPEN', 'LOW', 'Matériel',
     4, NOW() - INTERVAL 3 DAY, NULL, NULL),

(12, 'Certificat SSL expiré sur intranet',
     'Le navigateur affiche « Votre connexion n\'est pas privée » sur http://intranet.',
 'IN_PROGRESS', 'HIGH', 'Sécurité',
 4, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 6 DAY, NULL);


-- =============================================================================
--  NOTIFICATIONS
-- =============================================================================
INSERT INTO notifications
  (title, message, type, priority, created_by, is_read, username, ticket_id, created_at)
VALUES

-- Notifs admin (créations d'incidents)
    ('Nouvel incident : Lenteur sur l\'ERP',
 'alice@demo.com a créé un incident de priorité MEDIUM.',
 'CREATED', 'MEDIUM', 'alice@demo.com', 0,
 'admin@demo.com', 4, NOW() - INTERVAL 2 DAY),

('Nouvel incident : Badge refusé à l\'entrée',
 'bob@demo.com a créé un incident de priorité LOW.',
 'CREATED', 'LOW', 'bob@demo.com', 0,
 'admin@demo.com', 8, NOW() - INTERVAL 1 DAY),

('Nouvel incident : Clé USB non reconnue',
 'charlie@demo.com a créé un incident de priorité LOW.',
 'CREATED', 'LOW', 'charlie@demo.com', 0,
 'admin@demo.com', 11, NOW() - INTERVAL 3 DAY),

('Nouvel incident : Certificat SSL expiré',
 'charlie@demo.com a créé un incident de priorité HIGH.',
 'CREATED', 'HIGH', 'charlie@demo.com', 0,
 'admin@demo.com', 12, NOW() - INTERVAL 7 DAY),

-- Notifs users (mises à jour de statut)
('Votre incident "Accès VPN" est clôturé',
 'L\'incident a été résolu et clôturé par l\'administrateur.',
 'STATUS_UPDATE', 'HIGH', 'admin@demo.com', 1,
 'alice@demo.com', 2, NOW() - INTERVAL 40 DAY),

('Votre incident "Écran bloqué" est en cours',
 'Un technicien a pris en charge votre incident.',
 'STATUS_UPDATE', 'HIGH', 'admin@demo.com', 0,
 'alice@demo.com', 3, NOW() - INTERVAL 9 DAY),

('Votre incident "Excel" est en cours de traitement',
 'Le rollback de la mise à jour est en cours.',
 'STATUS_UPDATE', 'HIGH', 'admin@demo.com', 0,
 'bob@demo.com', 7, NOW() - INTERVAL 4 DAY);


-- =============================================================================
--  AUDIT LOGS
-- =============================================================================
INSERT INTO logs (action, entity_type, entity_id, performed_by, timestamp) VALUES
                                                                               ('CREATE_INCIDENT',  'Incident', 1,  2, NOW() - INTERVAL 50 DAY),
                                                                               ('UPDATE_STATUS',    'Incident', 1,  1, NOW() - INTERVAL 48 DAY),
                                                                               ('CREATE_INCIDENT',  'Incident', 2,  2, NOW() - INTERVAL 42 DAY),
                                                                               ('UPDATE_STATUS',    'Incident', 2,  1, NOW() - INTERVAL 40 DAY),
                                                                               ('CREATE_INCIDENT',  'Incident', 5,  3, NOW() - INTERVAL 38 DAY),
                                                                               ('UPDATE_STATUS',    'Incident', 5,  1, NOW() - INTERVAL 36 DAY),
                                                                               ('CREATE_INCIDENT',  'Incident', 9,  4, NOW() - INTERVAL 55 DAY),
                                                                               ('DELETE_INCIDENT',  'Incident', 9,  1, NOW() - INTERVAL 53 DAY),
                                                                               ('CREATE_USER',      'User',     3,  1, NOW() - INTERVAL 30 DAY),
                                                                               ('CREATE_USER',      'User',     4,  1, NOW() - INTERVAL 15 DAY),
                                                                               ('CREATE_INCIDENT',  'Incident', 12, 4, NOW() - INTERVAL 7 DAY),
                                                                               ('UPDATE_STATUS',    'Incident', 12, 1, NOW() - INTERVAL 6 DAY);


-- =============================================================================
--  Vérification rapide
-- =============================================================================
SELECT 'users'         AS `table`, COUNT(*) AS total FROM users
UNION ALL
SELECT 'incidents',     COUNT(*) FROM incidents
UNION ALL
SELECT 'notifications', COUNT(*) FROM notifications
UNION ALL
SELECT 'logs',          COUNT(*) FROM logs;
