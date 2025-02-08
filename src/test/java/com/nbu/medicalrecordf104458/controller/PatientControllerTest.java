package com.nbu.medicalrecordf104458.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecordf104458.config.JwtAuthFilter;
import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.PatientDto;
import com.nbu.medicalrecordf104458.service.PatientService;
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

import java.time.LocalDate;
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

@WebMvcTest(controllers = PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@MockBean(JwtAuthFilter.class) // Mocks out JWT filter so it's not used in tests
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    private PatientDto patientDto;

    @BeforeEach
    public void setup() {
        patientDto = new PatientDto(1L, "Patient", 123456789L,
                LocalDate.parse("2025-01-01"), 1L);
    }

    @Test
    public void testGetAllPatients() throws Exception {
        Set<PatientDto> patients = new HashSet<>();
        patients.add(patientDto);
        when(patientService.getAllPatients()).thenReturn(patients);

        mockMvc.perform(get("/patients/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetPatientById() throws Exception {
        Long patientId = 1L;

        when(patientService.getPatientById(patientId)).thenReturn(patientDto);

        mockMvc.perform(get("/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId));
    }

    @Test
    public void testCreatePatient() throws Exception {
        when(patientService.createPatient(any(PatientDto.class))).thenReturn(patientDto);

        mockMvc.perform(post("/patients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testUpdatePatient() throws Exception {
        Long patientId = 1L;

        when(patientService.updatePatient(eq(patientId), any(PatientDto.class))).thenReturn(patientDto);

        mockMvc.perform(put("/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId));
    }

    @Test
    public void testDeletePatient() throws Exception {
        Long patientId = 1L;

        mockMvc.perform(delete("/patients/{id}", patientId))
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckInsuranceStatus() throws Exception {
        Long patientId = 1L;

        when(patientService.isInsurancePaidLast6Months(patientId)).thenReturn(true);

        mockMvc.perform(get("/patients/{id}/insurance-status", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("The patient has paid their insurance in the last 6 months."));
    }

    @Test
    public void testGetPatientsByDiagnose() throws Exception {
        Long diagnoseId = 1L;
        Set<PatientDto> patients = new HashSet<>();
        patients.add(patientDto);

        when(patientService.getPatientsByDiagnoseId(diagnoseId)).thenReturn(patients);

        mockMvc.perform(get("/patients/patients-by-diagnose/{diagnoseId}", diagnoseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetPatientsByGeneralPractitioner() throws Exception {
        Long gpId = 1L;
        Set<PatientDto> patients = new HashSet<>();
        patients.add(patientDto);

        when(patientService.getPatientsByGeneralPractitioner(gpId)).thenReturn(patients);

        mockMvc.perform(get("/patients/patients-by-gp/{gpId}", gpId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetPatientVisits() throws Exception {
        Long patientId = 1L;
        Set<AppointmentDto> visits = new HashSet<>();
        visits.add(new AppointmentDto());

        when(patientService.getVisitsByPatient(patientId)).thenReturn(visits);

        mockMvc.perform(get("/patients/{id}/appointments", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());
    }

}
