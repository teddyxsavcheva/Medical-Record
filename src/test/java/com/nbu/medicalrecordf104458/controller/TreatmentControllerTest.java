package com.nbu.medicalrecordf104458.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecordf104458.config.JwtAuthFilter;
import com.nbu.medicalrecordf104458.dto.TreatmentDto;
import com.nbu.medicalrecordf104458.service.TreatmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TreatmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@MockBean(JwtAuthFilter.class) // Mocks out JWT filter so it's not used in tests
public class TreatmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TreatmentService treatmentService;

    private TreatmentDto treatmentDto;

    @BeforeEach
    public void setup() {
        treatmentDto = new TreatmentDto();
        treatmentDto.setId(1L);
        treatmentDto.setMedicineName("Chemotherapy");
        treatmentDto.setDosageAmount("1");
        treatmentDto.setFrequency("Weekly");
    }

    @Test
    public void testGetAllTreatments() throws Exception {
        Set<TreatmentDto> treatments = new HashSet<>();
        treatments.add(treatmentDto);
        when(treatmentService.getAllTreatments()).thenReturn(treatments);

        mockMvc.perform(get("/treatments/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetTreatmentById() throws Exception {
        Long treatmentId = 1L;

        when(treatmentService.getTreatmentById(treatmentId)).thenReturn(treatmentDto);

        mockMvc.perform(get("/treatments/{id}", treatmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(treatmentId));
    }

    @Test
    public void testCreateTreatment() throws Exception {
        when(treatmentService.createTreatment(any(TreatmentDto.class))).thenReturn(treatmentDto);

        mockMvc.perform(post("/treatments/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(treatmentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testUpdateTreatment() throws Exception {
        Long treatmentId = 1L;

        when(treatmentService.updateTreatment(eq(treatmentId), any(TreatmentDto.class))).thenReturn(treatmentDto);

        mockMvc.perform(put("/treatments/{id}", treatmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(treatmentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(treatmentId));
    }

    @Test
    public void testDeleteTreatment() throws Exception {
        Long treatmentId = 1L;

        mockMvc.perform(delete("/treatments/{id}", treatmentId))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddAppointmentToTreatment() throws Exception {
        Long treatmentId = 1L;
        Long appointmentId = 1L;

        when(treatmentService.addAppointment(treatmentId, appointmentId)).thenReturn(treatmentDto);

        mockMvc.perform(post("/treatments/{treatmentId}/appointments/{appointmentId}", treatmentId, appointmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(treatmentId));
    }

    @Test
    public void testRemoveAppointmentFromTreatment() throws Exception {
        Long treatmentId = 1L;
        Long appointmentId = 1L;

        when(treatmentService.removeAppointment(treatmentId, appointmentId)).thenReturn(treatmentDto);

        mockMvc.perform(delete("/treatments/{treatmentId}/appointments/{appointmentId}", treatmentId, appointmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(treatmentId));
    }
}
