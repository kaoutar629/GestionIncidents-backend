package com.kaoutar.gestionIncidents.controller;

import com.kaoutar.gestionIncidents.dto.Request.LoginRequest;
import com.kaoutar.gestionIncidents.dto.Request.RegisterRequest;
import com.kaoutar.gestionIncidents.dto.Response.JwtResponse;
import com.kaoutar.gestionIncidents.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

}
