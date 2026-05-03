package com.kaoutar.gestionIncidents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaoutar.gestionIncidents.dto.Request.LoginRequest;
import com.kaoutar.gestionIncidents.dto.Request.RegisterRequest;
import com.kaoutar.gestionIncidents.dto.Response.JwtResponse;
import com.kaoutar.gestionIncidents.security.JwtAuthFilter;
import com.kaoutar.gestionIncidents.security.JwtUtils;
import com.kaoutar.gestionIncidents.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)   // ← disables Spring Security filter chain
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123456");

        JwtResponse response = new JwtResponse();
        response.setToken("fake-jwt-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void shouldRegister() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123456");

        when(authService.register(any(RegisterRequest.class))).thenReturn("User registered");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered"));
    }
}
