-- =============================================================================
--  GestionIncidents — Schema propre
--  MySQL 8.0+  |  Base : gestion_incidents
--  Utilisation : remplace le DDL généré automatiquement par Hibernate (ddl-auto=none)
-- =============================================================================

-- Créer la base si elle n'existe pas encore
CREATE DATABASE IF NOT EXISTS gestion_incidents
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gestion_incidents;

SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================================
--  TABLE : users
-- =============================================================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
                       id          BIGINT        NOT NULL AUTO_INCREMENT,
                       email       VARCHAR(255)  NOT NULL,
                       password    VARCHAR(255)  NOT NULL,
                       first_name  VARCHAR(100),
                       last_name   VARCHAR(100),
                       role        ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
                       created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       PRIMARY KEY (id),

    -- Unicité de l'email (aussi utilisée comme login Spring Security)
                       CONSTRAINT uq_users_email UNIQUE (email),

    -- Index pour les recherches par rôle (filtrer les admins lors de notifications)
                       INDEX idx_users_role (role),

    -- Index composite pour les tris dashboard : rôle + date de création
                       INDEX idx_users_role_created (role, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
--  TABLE : incidents
-- =============================================================================
DROP TABLE IF EXISTS incidents;
CREATE TABLE incidents (
                           id           BIGINT        NOT NULL AUTO_INCREMENT,
                           title        VARCHAR(500)  NOT NULL,
                           description  TEXT,
                           status       ENUM('OPEN','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'OPEN',
                           priority     ENUM('LOW','MEDIUM','HIGH')                     NOT NULL DEFAULT 'LOW',
                           category     VARCHAR(100),
                           created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at   DATETIME      ON UPDATE CURRENT_TIMESTAMP,
                           resolved_at  DATETIME,
                           image_base64 MEDIUMTEXT,          -- jusqu'à ~16 MB encodé en base64
                           created_by   BIGINT,              -- FK → users.id (nullable : archivage)

                           PRIMARY KEY (id),

    -- Contrainte FK avec SET NULL pour conserver l'incident si l'user est supprimé
                           CONSTRAINT fk_incidents_user
                               FOREIGN KEY (created_by) REFERENCES users(id)
                                   ON DELETE SET NULL
                                   ON UPDATE CASCADE,

    -- Index sur le créateur : liste "mes incidents" (requête très fréquente)
                           INDEX idx_incidents_created_by (created_by),

    -- Index sur le statut : filtres dashboard + transitions
                           INDEX idx_incidents_status (status),

    -- Index sur la priorité : filtres dashboard
                           INDEX idx_incidents_priority (priority),

    -- Index sur la date de création : tri chronologique (desc)
                           INDEX idx_incidents_created_at (created_at DESC),

    -- Index composite : liste filtrée par user + statut (requête la plus courante pour les USERs)
                           INDEX idx_incidents_user_status (created_by, status),

    -- Index composite : filtre admin par statut + priorité
                           INDEX idx_incidents_status_priority (status, priority),

    -- Contrainte CHECK : resolved_at ne peut être défini que si status = RESOLVED ou CLOSED
                           CONSTRAINT chk_resolved_at
                               CHECK (resolved_at IS NULL OR status IN ('RESOLVED','CLOSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;





-- =============================================================================
--  TABLE : notifications
-- =============================================================================
DROP TABLE IF EXISTS notifications;
CREATE TABLE notifications (
                               id          BIGINT       NOT NULL AUTO_INCREMENT,
                               title       VARCHAR(500),
                               message     TEXT,
                               type        VARCHAR(50),          -- CREATED | STATUS_UPDATE
                               priority    VARCHAR(20),          -- LOW | MEDIUM | HIGH (copie dénormalisée)
                               created_by  VARCHAR(255),         -- email du créateur (dénormalisé pour affichage)
                               is_read     TINYINT(1)   NOT NULL DEFAULT 0,
                               username    VARCHAR(255),         -- email du destinataire
                               ticket_id   BIGINT,               -- référence souple (pas de FK : survit à la suppression)
                               created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               PRIMARY KEY (id),

    -- Index principal : toutes les notifs d'un destinataire, non lues en premier
                               INDEX idx_notif_username_read (username, is_read),

    -- Index pour requête "notifs d'un ticket"
                               INDEX idx_notif_ticket (ticket_id),

    -- Index pour nettoyage automatique des anciennes notifs
                               INDEX idx_notif_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
--  TABLE : logs  (audit)
-- =============================================================================
DROP TABLE IF EXISTS logs;
CREATE TABLE logs (
                      id           BIGINT      NOT NULL AUTO_INCREMENT,
                      action       VARCHAR(100),        -- CREATE_INCIDENT | UPDATE_STATUS | DELETE_USER …
                      entity_type  VARCHAR(100),        -- Incident | User | Comment
                      entity_id    BIGINT,
                      performed_by BIGINT,              -- id de l'user qui a effectué l'action
                      timestamp    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

                      PRIMARY KEY (id),

    -- Index : audit trail d'une entité donnée
                      INDEX idx_logs_entity (entity_type, entity_id),

    -- Index : actions d'un utilisateur
                      INDEX idx_logs_performed_by (performed_by),

    -- Index : recherche chronologique
                      INDEX idx_logs_timestamp (timestamp DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
--  Fin du schéma
-- =============================================================================
