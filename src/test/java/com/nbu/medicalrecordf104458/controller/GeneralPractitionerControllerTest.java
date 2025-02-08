package com.nbu.medicalrecordf104458.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecordf104458.config.JwtAuthFilter;
import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.dto.GeneralPractitionerDto;
import com.nbu.medicalrecordf104458.dto.queries.GpPatientsCountDto;
import com.nbu.medicalrecordf104458.service.GeneralPractitionerService;
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

@WebMvcTest(controllers = GeneralPractitionerController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@MockBean(JwtAuthFilter.class) // Mocks out JWT filter so it's not used in tests
public class GeneralPractitionerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GeneralPractitionerService gpService;

    private GeneralPractitionerDto gpDto;

    @BeforeEach
    public void setup() {
        gpDto = new GeneralPractitionerDto();
        gpDto.setDoctor(new DoctorDto(1L, "Dr. Doctorov", Set.of(1L)));
    }

    @Test
    public void testGetAllDoctors() throws Exception {
        when(gpService.getAllDoctors()).thenReturn(Set.of(gpDto));

        mockMvc.perform(get("/general-practitioners/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].doctor.name").value(gpDto.getDoctor().getName()));
    }

    @Test
    public void testGetDoctorById() throws Exception {
        Long doctorId = 1L;

        when(gpService.getDoctorById(doctorId)).thenReturn(gpDto);

        mockMvc.perform(get("/general-practitioners/{id}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctor.id").value(doctorId));
    }

    @Test
    public void testCreateDoctor() throws Exception {
        when(gpService.createDoctor(any(GeneralPractitionerDto.class))).thenReturn(gpDto);

        mockMvc.perform(post("/general-practitioners/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gpDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.doctor.id").value(1L));
    }

    @Test
    public void testUpdateDoctor() throws Exception {
        Long doctorId = 1L;

        when(gpService.updateDoctor(eq(doctorId), any(GeneralPractitionerDto.class))).thenReturn(gpDto);

        mockMvc.perform(put("/general-practitioners/{id}", doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gpDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctor.id").value(doctorId));
    }

    @Test
    public void testDeleteDoctor() throws Exception {
        Long doctorId = 1L;

        mockMvc.perform(delete("/general-practitioners/{id}", doctorId))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddSpecialization() throws Exception {
        Long gpId = 1L;
        Long specializationId = 1L;

        when(gpService.addSpecialization(gpId, specializationId)).thenReturn(gpDto);

        mockMvc.perform(post("/general-practitioners/{gpId}/specializations/{specializationId}", gpId, specializationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctor.id").value(gpId));
    }

    @Test
    public void testRemoveSpecialization() throws Exception {
        Long gpId = 1L;
        Long specializationId = 2L;

        when(gpService.removeSpecialization(gpId, specializationId)).thenReturn(gpDto);

        mockMvc.perform(delete("/general-practitioners/{gpId}/specializations/{specializationId}", gpId, specializationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctor.id").value(gpId));
    }

    @Test
    public void testAddPatient() throws Exception {
        Long gpId = 1L;
        Long patientId = 2L;

        when(gpService.addPatient(gpId, patientId)).thenReturn(gpDto);

        mockMvc.perform(post("/general-practitioners/{gpId}/patients/{patientId}", gpId, patientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctor.id").value(gpId));
    }

    @Test
    public void testRemovePatient() throws Exception {
        Long gpId = 1L;
        Long patientId = 2L;

        when(gpService.removePatient(gpId, patientId)).thenReturn(gpDto);

        mockMvc.perform(delete("/general-practitioners/{gpId}/patients/{patientId}", gpId, patientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctor.id").value(gpId));
    }

    @Test
    public void testGetAllGeneralPractitionersWithPatientCount() throws Exception {
        Set<GpPatientsCountDto> counts = new HashSet<>();
        counts.add(new GpPatientsCountDto());

        when(gpService.getAllGeneralPractitionersWithPatientCount()).thenReturn(counts);

        mockMvc.perform(get("/general-practitioners/patients-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    public void testGetGeneralPractitionerWithPatientCount() throws Exception {
        Long gpId = 1L;
        GpPatientsCountDto countDto = new GpPatientsCountDto();
        countDto.setDoctorId(gpId);

        when(gpService.getGeneralPractitionerWithPatientCount(gpId)).thenReturn(countDto);

        mockMvc.perform(get("/general-practitioners/{id}/patients-count", gpId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(gpId));
    }
}

