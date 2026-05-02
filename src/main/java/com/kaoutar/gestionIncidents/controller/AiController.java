package com.kaoutar.gestionIncidents.controller;

import com.kaoutar.gestionIncidents.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/classify")
    public ResponseEntity<?> classify(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                aiService.classifyIncident(
                        body.get("title"),
                        body.get("description")
                )
        );
    }
}