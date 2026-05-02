package com.kaoutar.gestionIncidents.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiService {

    public Map<String, Object> classifyIncident(String title, String description) {

        String text = ((title == null ? "" : title) + " " +
                (description == null ? "" : description)).toLowerCase();

        String priority = "LOW";
        String category = "Autre";
        String explanation = "";

        // ─────────────────────────────
        // PRIORITÉ
        // ─────────────────────────────
        if (
                text.contains("down") ||
                        text.contains("bloqué") ||
                        text.contains("erreur 500") ||
                        text.contains("ne fonctionne plus") ||
                        text.contains("plus accès") ||
                        text.contains("serveur") ||
                        text.contains("crash")
        ) {
            priority = "HIGH";
            explanation = "Incident critique impactant le fonctionnement du système";
        }
        else if (
                text.contains("lent") ||
                        text.contains("problème") ||
                        text.contains("bug") ||
                        text.contains("erreur")
        ) {
            priority = "MEDIUM";
            explanation = "Incident gênant mais avec impact limité";
        }
        else {
            priority = "LOW";
            explanation = "Incident mineur ou demande d'information";
        }

        // ─────────────────────────────
        // CATÉGORIE
        // ─────────────────────────────
        if (
                text.contains("réseau") ||
                        text.contains("connexion") ||
                        text.contains("wifi") ||
                        text.contains("internet")
        ) {
            category = "Réseau";
        }
        else if (
                text.contains("imprimante") ||
                        text.contains("pc") ||
                        text.contains("ordinateur") ||
                        text.contains("écran")
        ) {
            category = "Matériel";
        }
        else if (
                text.contains("application") ||
                        text.contains("logiciel") ||
                        text.contains("bug") ||
                        text.contains("erreur")
        ) {
            category = "Logiciel";
        }
        else if (
                text.contains("mot de passe") ||
                        text.contains("login") ||
                        text.contains("compte") ||
                        text.contains("accès refusé")
        ) {
            category = "Accès";
        }
        else if (
                text.contains("virus") ||
                        text.contains("hack") ||
                        text.contains("attaque") ||
                        text.contains("sécurité")
        ) {
            category = "Sécurité";
        }
        else if (
                text.contains("lent") ||
                        text.contains("performance")
        ) {
            category = "Performance";
        }

        // ─────────────────────────────
        // RESULT
        // ─────────────────────────────
        Map<String, Object> result = new HashMap<>();
        result.put("priority", priority);
        result.put("category", category);
        result.put("explanation", explanation);

        return result;
    }
}