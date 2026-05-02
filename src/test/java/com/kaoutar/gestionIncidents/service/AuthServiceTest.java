package com.kaoutar.gestionIncidents.service;

import com.kaoutar.gestionIncidents.dto.Request.LoginRequest;
import com.kaoutar.gestionIncidents.dto.Request.RegisterRequest;
import com.kaoutar.gestionIncidents.dto.Response.JwtResponse;
import com.kaoutar.gestionIncidents.entity.User;
import com.kaoutar.gestionIncidents.repository.UserRepository;
import com.kaoutar.gestionIncidents.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    // ✅ LOGIN
    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getUsername())
                .thenReturn("test@mail.com");

        when(jwtUtils.generateJwtToken(userDetails))
                .thenReturn("fake-token");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        JwtResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-token", response.getToken());
        assertEquals("test@mail.com", response.getEmail());
    }

    // ✅ REGISTER SUCCESS
    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@mail.com");
        request.setPassword("123");

        when(userRepository.findByEmail("new@mail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123"))
                .thenReturn("encoded");

        String result = authService.register(request);

        assertEquals("Utilisateur enregistré avec succès", result);
        verify(userRepository).save(any(User.class));
    }


    @Test
    void shouldThrowIfEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });
    }
}

