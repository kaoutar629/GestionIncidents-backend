package com.kaoutar.gestionIncidents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaoutar.gestionIncidents.dto.CreateIncidentDto;
import com.kaoutar.gestionIncidents.dto.IncidentDto;
import com.kaoutar.gestionIncidents.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncidentController.class)
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncidentService incidentService;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ GET ALL
    @Test
    @WithMockUser(username = "user@mail.com", roles = {"USER"})
    void shouldGetAllIncidents() throws Exception {
        IncidentDto dto = new IncidentDto();
        dto.setTitle("Bug");

        when(incidentService.getAllIncidents(0, 10, "user@mail.com"))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Bug"));
    }

    // ✅ CREATE
    @Test
    @WithMockUser(username = "user@mail.com", roles = {"USER"})
    void shouldCreateIncident() throws Exception {
        CreateIncidentDto request = new CreateIncidentDto();
        request.setTitle("Bug");

        IncidentDto response = new IncidentDto();
        response.setTitle("Bug");

        when(incidentService.createIncident(request)).thenReturn(response);

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Bug"));
    }

    // ✅ DELETE
    @Test
    @WithMockUser(username = "admin@mail.com", roles = {"ADMIN"})
    void shouldDeleteIncident() throws Exception {

        mockMvc.perform(delete("/api/incidents/1"))
                .andExpect(status().isNoContent());
    }
}