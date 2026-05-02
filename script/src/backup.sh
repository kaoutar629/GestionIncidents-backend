#!/usr/bin/env bash
# =============================================================================
#  GestionIncidents — Stratégie de backup MySQL
#  backup.sh
#
#  Usage :
#    chmod +x backup.sh
#    ./backup.sh                     # backup complet maintenant
#    ./backup.sh --restore latest    # restaurer le dernier backup
#    ./backup.sh --list              # lister les backups disponibles
#    ./backup.sh --clean             # supprimer les backups > RETENTION_DAYS
#
#  Automatisation (cron) :
#    crontab -e
#    0 2 * * *  /opt/gestion-incidents/backup.sh >> /var/log/gi_backup.log 2>&1
#    (exécuté chaque nuit à 2h00)
# =============================================================================

set -euo pipefail

# ── Configuration ─────────────────────────────────────────────────────────────
DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3307}"
DB_NAME="${DB_NAME:-gestion_incidents}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-}"                  # laisser vide → utilise ~/.my.cnf

BACKUP_DIR="${BACKUP_DIR:-/var/backups/gestion_incidents}"
RETENTION_DAYS="${RETENTION_DAYS:-30}"  # durée de conservation (jours)

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/gi_${TIMESTAMP}.sql.gz"
LOG_PREFIX="[$(date '+%Y-%m-%d %H:%M:%S')] [BACKUP]"

# ── Helpers ───────────────────────────────────────────────────────────────────
log()  { echo "${LOG_PREFIX} $*"; }
err()  { echo "${LOG_PREFIX} ERROR: $*" >&2; exit 1; }
need() { command -v "$1" &>/dev/null || err "'$1' introuvable. Installez : $2"; }

# ── Vérifications des dépendances ─────────────────────────────────────────────
need mysqldump "sudo apt install mysql-client"
need gzip      "sudo apt install gzip"
need mysql     "sudo apt install mysql-client"

# ── Répertoire de destination ──────────────────────────────────────────────────
mkdir -p "${BACKUP_DIR}"

# ── Paramètre mot de passe ─────────────────────────────────────────────────────
if [[ -n "${DB_PASS}" ]]; then
    MYSQL_PWD="${DB_PASS}"
    export MYSQL_PWD
fi

# =============================================================================
#  ACTIONS
# =============================================================================

# ── --list : afficher les backups disponibles ──────────────────────────────────
if [[ "${1:-}" == "--list" ]]; then
    log "Backups disponibles dans ${BACKUP_DIR} :"
    ls -lh "${BACKUP_DIR}"/gi_*.sql.gz 2>/dev/null \
        | awk '{print $5, $9}' \
        || echo "  (aucun backup trouvé)"
    exit 0
fi

# ── --clean : purger les vieux backups ─────────────────────────────────────────
if [[ "${1:-}" == "--clean" ]]; then
    log "Suppression des backups de plus de ${RETENTION_DAYS} jours…"
    find "${BACKUP_DIR}" -name "gi_*.sql.gz" \
         -mtime "+${RETENTION_DAYS}" -delete -print \
        | sed "s/^/  Supprimé : /"
    log "Nettoyage terminé."
    exit 0
fi

# ── --restore <fichier|"latest"> : restaurer un backup ────────────────────────
if [[ "${1:-}" == "--restore" ]]; then
    TARGET="${2:-latest}"

    if [[ "${TARGET}" == "latest" ]]; then
        RESTORE_FILE=$(ls -t "${BACKUP_DIR}"/gi_*.sql.gz 2>/dev/null | head -1)
        [[ -z "${RESTORE_FILE}" ]] && err "Aucun backup trouvé dans ${BACKUP_DIR}"
    else
        RESTORE_FILE="${TARGET}"
    fi

    [[ -f "${RESTORE_FILE}" ]] || err "Fichier introuvable : ${RESTORE_FILE}"

    log "Restauration depuis : ${RESTORE_FILE}"
    log "Base cible          : ${DB_NAME} sur ${DB_HOST}:${DB_PORT}"
    read -rp "  ⚠️  Cela écrasera '${DB_NAME}'. Continuer ? [y/N] " CONFIRM
    [[ "${CONFIRM,,}" == "y" ]] || { log "Restauration annulée."; exit 0; }

    # Recréer la base et importer
    mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" \
          -e "DROP DATABASE IF EXISTS \`${DB_NAME}\`; \
              CREATE DATABASE \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

    gunzip -c "${RESTORE_FILE}" \
        | mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" "${DB_NAME}"

    log "✅ Restauration terminée depuis ${RESTORE_FILE}"
    exit 0
fi

# =============================================================================
#  BACKUP COMPLET (action par défaut)
# =============================================================================
log "Démarrage du backup — base : ${DB_NAME}"

# mysqldump avec options robustes :
#   --single-transaction  : snapshot cohérent InnoDB sans verrouillage
#   --routines / --events : inclut les procédures et events MySQL
#   --hex-blob            : sérialisation binaire correcte
#   --set-gtid-purged=OFF : évite les erreurs sur les réplicas sans GTID
mysqldump \
    -h "${DB_HOST}" \
    -P "${DB_PORT}" \
    -u "${DB_USER}" \
    --single-transaction \
    --routines \
    --events \
    --hex-blob \
    --set-gtid-purged=OFF \
    --databases "${DB_NAME}" \
| gzip -9 > "${BACKUP_FILE}"

SIZE=$(du -sh "${BACKUP_FILE}" | cut -f1)
log "✅ Backup créé : ${BACKUP_FILE} (${SIZE})"

# ── Vérification rapide de l'intégrité ────────────────────────────────────────
gunzip -t "${BACKUP_FILE}" \
    && log "✅ Vérification de l'intégrité : OK" \
    || err "Backup corrompu ! Fichier : ${BACKUP_FILE}"

# ── Rotation : supprimer les backups plus vieux que RETENTION_DAYS ─────────────
DELETED=$(find "${BACKUP_DIR}" -name "gi_*.sql.gz" -mtime "+${RETENTION_DAYS}" -delete -print | wc -l)
[[ "${DELETED}" -gt 0 ]] && log "Rotation : ${DELETED} ancien(s) backup(s) supprimé(s)"

# ── Résumé final ───────────────────────────────────────────────────────────────
TOTAL=$(ls "${BACKUP_DIR}"/gi_*.sql.gz 2>/dev/null | wc -l)
log "Total backups conservés : ${TOTAL} (rétention : ${RETENTION_DAYS} jours)"
log "Prochain backup prévu   : $(date -d 'tomorrow 02:00' '+%Y-%m-%d 02:00' 2>/dev/null || echo 'voir crontab')"
