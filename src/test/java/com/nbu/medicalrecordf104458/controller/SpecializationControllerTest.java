package com.nbu.medicalrecordf104458.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecordf104458.config.JwtAuthFilter;
import com.nbu.medicalrecordf104458.dto.SpecializationDto;
import com.nbu.medicalrecordf104458.service.SpecializationService;
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

@WebMvcTest(controllers = SpecializationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@MockBean(JwtAuthFilter.class) // Mocks out JWT filter so it's not used in tests
public class SpecializationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpecializationService specializationService;

    private SpecializationDto specializationDto;

    @BeforeEach
    public void setup() {
        specializationDto = new SpecializationDto();
        specializationDto.setId(1L);
        specializationDto.setName("Cardiology");
    }

    @Test
    public void testGetAllSpecializations() throws Exception {
        Set<SpecializationDto> specializations = new HashSet<>();
        specializations.add(specializationDto);
        when(specializationService.getAllSpecializations()).thenReturn(specializations);

        mockMvc.perform(get("/specializations/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetSpecializationById() throws Exception {
        Long specializationId = 1L;

        when(specializationService.getSpecializationById(specializationId)).thenReturn(specializationDto);

        mockMvc.perform(get("/specializations/{id}", specializationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specializationId));
    }

    @Test
    public void testCreateSpecialization() throws Exception {
        when(specializationService.createSpecialization(any(SpecializationDto.class))).thenReturn(specializationDto);

        mockMvc.perform(post("/specializations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specializationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testUpdateSpecialization() throws Exception {
        Long specializationId = 1L;

        when(specializationService.updateSpecialization(eq(specializationId), any(SpecializationDto.class))).thenReturn(specializationDto);

        mockMvc.perform(put("/specializations/{id}", specializationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specializationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specializationId));
    }

    @Test
    public void testDeleteSpecialization() throws Exception {
        Long specializationId = 1L;

        mockMvc.perform(delete("/specializations/{id}", specializationId))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddDoctorToSpecialization() throws Exception {
        Long specializationId = 1L;
        Long doctorId = 1L;

        when(specializationService.addDoctor(specializationId, doctorId)).thenReturn(specializationDto);

        mockMvc.perform(post("/specializations/{specializationId}/doctors/{doctorId}", specializationId, doctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specializationId));
    }

    @Test
    public void testRemoveDoctorFromSpecialization() throws Exception {
        Long specializationId = 1L;
        Long doctorId = 1L;

        when(specializationService.removeDoctor(specializationId, doctorId)).thenReturn(specializationDto);

        mockMvc.perform(delete("/specializations/{specializationId}/doctors/{doctorId}", specializationId, doctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specializationId));
    }
}
