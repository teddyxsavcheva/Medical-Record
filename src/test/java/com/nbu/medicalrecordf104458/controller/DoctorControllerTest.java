package com.nbu.medicalrecordf104458.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecordf104458.config.JwtAuthFilter;
import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.dto.queries.DoctorAppointmentsCountDto;
import com.nbu.medicalrecordf104458.service.DoctorService;
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

@WebMvcTest(controllers = DoctorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@MockBean(JwtAuthFilter.class) // Mocks out JWT filter so it's not used in tests
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorService doctorService;

    private DoctorDto doctorDto;
    private DoctorAppointmentsCountDto doctorAppointmentsCountDto;
    private Set<DoctorDto> mockDoctors;

    @BeforeEach
    public void setUp() {
        doctorDto = new DoctorDto(1L, "Dr. Doctorov", Set.of(1L));
        doctorAppointmentsCountDto = new DoctorAppointmentsCountDto(1L, "Dr. Doctorov", 10L);
        mockDoctors = Set.of(doctorDto);
    }

    @Test
    public void testGetAllDoctors() throws Exception {
        when(doctorService.getAllDoctors()).thenReturn(mockDoctors);

        mockMvc.perform(get("/doctors/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id", is(1)))
                .andExpect(jsonPath("[0].name", is("Dr. Doctorov")));
    }

    @Test
    public void testGetDoctorById() throws Exception {
        when(doctorService.getDoctorById(1L)).thenReturn(doctorDto);

        mockMvc.perform(get("/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Dr. Doctorov")));
    }

    @Test
    public void testGetDoctorById_NotFound() throws Exception {
        when(doctorService.getDoctorById(1L)).thenThrow(new EntityNotFoundException("No doctor found with id: 1"));

        mockMvc.perform(get("/doctors/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateDoctor() throws Exception {
        DoctorDto createdDto = new DoctorDto(1L, "Dr. New", Set.of(1L));

        when(doctorService.createDoctor(any(DoctorDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/doctors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Dr. New")));
    }

    @Test
    public void testUpdateDoctor() throws Exception {
        DoctorDto updatedDto = new DoctorDto(1L, "Dr. Updated", Set.of(1L));

        when(doctorService.updateDoctor(eq(1L), any(DoctorDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/doctors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Dr. Updated")));
    }

    @Test
    public void testDeleteDoctor() throws Exception {
        doNothing().when(doctorService).deleteDoctor(1L);

        mockMvc.perform(delete("/doctors/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllDoctorsWithAppointmentCount() throws Exception {
        Set<DoctorAppointmentsCountDto> doctorAppointmentsCountDtos = Set.of(doctorAppointmentsCountDto);

        when(doctorService.getAllDoctorsWithAppointmentCount()).thenReturn(doctorAppointmentsCountDtos);

        mockMvc.perform(get("/doctors/appointments-count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].doctorId", is(1)))
                .andExpect(jsonPath("[0].appointmentsCount", is(10)));
    }

    @Test
    public void testGetDoctorWithAppointmentCount() throws Exception {
        when(doctorService.getDoctorWithAppointmentCount(1L)).thenReturn(doctorAppointmentsCountDto);

        mockMvc.perform(get("/doctors/1/appointments-count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.doctorId", is(1)))
                .andExpect(jsonPath("$.appointmentsCount", is(10)));
    }

    @Test
    public void testGetDoctorsWithMostSickLeaves() throws Exception {
        when(doctorService.findDoctorsWithMostSickLeaves()).thenReturn(mockDoctors);

        mockMvc.perform(get("/doctors/doctors-with-most-sick-leaves"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id", is(1)))
                .andExpect(jsonPath("[0].name", is("Dr. Doctorov")));
    }

    @Test
    public void testAddSpecialization() throws Exception {
        Long doctorId = 1L;
        Long specializationId = 2L;
        doctorDto.setSpecializationIds(Set.of(1L, 2L));

        when(doctorService.addSpecialization(doctorId, specializationId)).thenReturn(doctorDto);

        mockMvc.perform(post("/doctors/{doctorId}/specializations/{specializationId}", doctorId, specializationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctorId))
                .andExpect(jsonPath("$.specializationIds").isArray())
                .andExpect(jsonPath("$.specializationIds.size()").value(2));
    }

    @Test
    public void testRemoveSpecialization() throws Exception {
        Long doctorId = 1L;
        Long specializationId = 2L;

        when(doctorService.removeSpecialization(doctorId, specializationId)).thenReturn(doctorDto);

        mockMvc.perform(delete("/doctors/{doctorId}/specializations/{specializationId}", doctorId, specializationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctorId))
                .andExpect(jsonPath("$.specializationIds").isArray())
                .andExpect(jsonPath("$.specializationIds[0]").value(1L));
    }
}
