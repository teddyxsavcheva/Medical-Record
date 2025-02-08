package com.nbu.medicalrecordf104458.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecordf104458.config.JwtAuthFilter;
import com.nbu.medicalrecordf104458.dto.DiagnoseDto;
import com.nbu.medicalrecordf104458.service.DiagnoseService;
import jakarta.persistence.EntityNotFoundException;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DiagnoseController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@MockBean(JwtAuthFilter.class) // Mocks out JWT filter so it's not used in tests
public class DiagnoseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DiagnoseService diagnoseService;

    private DiagnoseDto fluDto;
    private DiagnoseDto allergyDto;
    private Set<DiagnoseDto> mockDiagnoses;

    @BeforeEach
    public void setUp() {
        fluDto = new DiagnoseDto(1L,
                "Flu",
                "A contagious respiratory illness caused by influenza viruses, " +
                        "leading to fever, cough, sore throat, body aches, and fatigue.",
                new HashSet<>());

        allergyDto = new DiagnoseDto(2L, "Allergy", "An immune system reaction to substances (allergens) like pollen, dust, " +
                "or certain foods, which can cause symptoms ranging from sneezing and itching to more severe reactions such as anaphylaxis.", new HashSet<>());

        mockDiagnoses = Set.of(fluDto, allergyDto);

    }

    @Test
    public void testGetAllDiagnoses() throws Exception {
        when(diagnoseService.getAllDiagnoses()).thenReturn(Set.of(fluDto));

        mockMvc.perform(get("/diagnoses/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("[0].id", is(1)))
                .andExpect(jsonPath("[0].name", is("Flu")));
    }

    @Test
    public void testGetDiagnoseById() throws Exception {
        when(diagnoseService.getDiagnoseById(1L)).thenReturn(fluDto);

        mockMvc.perform(get("/diagnoses/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Flu")));
    }

    @Test
    public void testGetDiagnoseById_NotFound() throws Exception {
        when(diagnoseService.getDiagnoseById(1L)).thenThrow(new EntityNotFoundException("No diagnose found with id: 1"));

        mockMvc.perform(get("/diagnoses/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateDiagnose() throws Exception {
        DiagnoseDto createdDto = new DiagnoseDto(1L, "New", "New", new HashSet<>());

        when(diagnoseService.createDiagnose(any(DiagnoseDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/diagnoses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New")));
    }

    @Test
    public void testUpdateDiagnose() throws Exception {
        DiagnoseDto updatedDto = new DiagnoseDto(1L, "Updated Flu", "Updated Flu", new HashSet<>());

        when(diagnoseService.updateDiagnose(eq(1L),any(DiagnoseDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/diagnoses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Flu")));
    }

    @Test
    public void testDeleteDiagnose() throws Exception {
        doNothing().when(diagnoseService).deleteDiagnose(1L);

        mockMvc.perform(delete("/diagnoses/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddAppointmentToDiagnose() throws Exception {
        DiagnoseDto updatedDto = new DiagnoseDto(1L, "Flu", "Flu", new HashSet<>());

        when(diagnoseService.addAppointment(1L, 1L)).thenReturn(updatedDto);

        mockMvc.perform(post("/diagnoses/1/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Flu")));
    }

    @Test
    public void testRemoveAppointmentFromDiagnose() throws Exception {
        DiagnoseDto updatedDto = new DiagnoseDto(1L, "Flu", "Flu", new HashSet<>(Set.of(1L)));

        when(diagnoseService.removeAppointment(1L, 1L)).thenReturn(updatedDto);

        mockMvc.perform(delete("/diagnoses/1/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Flu")));
    }

    @Test
    public void testGetMostCommonDiagnoses() throws Exception {
        Set<DiagnoseDto> mockDiagnoses = Set.of(fluDto);
        when(diagnoseService.findMostCommonDiagnoses()).thenReturn(mockDiagnoses);

        mockMvc.perform(get("/diagnoses/most-common-diagnoses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Flu")));
    }
}
