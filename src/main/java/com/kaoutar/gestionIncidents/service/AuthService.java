package com.kaoutar.gestionIncidents.service;

import com.kaoutar.gestionIncidents.dto.Request.LoginRequest;
import com.kaoutar.gestionIncidents.dto.Request.RegisterRequest;
import com.kaoutar.gestionIncidents.dto.Response.JwtResponse;
import com.kaoutar.gestionIncidents.entity.User;
import com.kaoutar.gestionIncidents.repository.UserRepository;
import com.kaoutar.gestionIncidents.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest request) {

        // 1. Authentification Spring Security
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim(),
                        request.getPassword()
                )
        );

        // 2. ✅ FIX : cast vers UserDetails (interface) pas User directement
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // 3. Génération du token JWT
        String token = jwtUtils.generateJwtToken(userDetails);

        // 4. Rechargement de l'entité User pour avoir id, role, etc.
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User introuvable après auth"));

        // 5. Construction de la réponse
        JwtResponse response = new JwtResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole() != null ? user.getRole().name() : "USER");
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());

        return response;
    }

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé : " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        userRepository.save(user);
        return "Utilisateur enregistré avec succès";
    }
}
