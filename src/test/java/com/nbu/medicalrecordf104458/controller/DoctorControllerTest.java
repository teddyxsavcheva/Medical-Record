package com.nbu.medicalrecordf104458.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecordf104458.config.JwtAuthFilter;
import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.DiagnoseDto;
import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.dto.queries.DoctorAppointmentsCountDto;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.model.Treatment;
import com.nbu.medicalrecordf104458.service.DoctorService;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DoctorAppointmentController.class)
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

    private Doctor doctor;
    private DoctorDto doctorDto;
    private AppointmentDto appointmentDto;
    private Diagnose diagnose;
    private DoctorAppointment appointment;
    private Specialization specialization;
    private GeneralPractitioner gp;
    private Patient patient;
    private SickLeave sickLeave;
    private Treatment treatment;

    @BeforeEach
    public void setUp() {
        diagnose = new Diagnose();
        diagnose.setId(1L);
        diagnose.setName("Flu");
        diagnose.setDescription("A contagious respiratory illness.");
        diagnose.setDeleted(false);
        diagnose.setAppointments(new HashSet<>());

        specialization = new Specialization();
        specialization.setId(1L);
        specialization.setName("Cardiology");
        specialization.setDeleted(false);
        specialization.setDoctors(new HashSet<>());

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Doctorov");
        doctor.setAppointments(new HashSet<>());
        doctor.setDeleted(false);
        doctor.setSpecializations(new HashSet<>(Set.of(specialization)));

        doctorDto = new DoctorDto();
        doctorDto.setId(1L);
        doctorDto.setName("Dr. Doctorov");
        doctorDto.setSpecializationIds(new HashSet<>(Set.of(specialization.getId())));

        gp = new GeneralPractitioner();
        gp.setId(2L);
        gp.setName("Dr. Lekar");
        gp.setSpecializations(new HashSet<>(Set.of(specialization)));

        patient = new Patient();
        patient.setId(1L);
        patient.setName("Pacientov");
        patient.setFamilyDoctor(gp);
        patient.setLastInsurancePayment(LocalDate.of(2025, 1, 31));
        patient.setUnifiedCivilNumber(1234L);

        appointment = new DoctorAppointment();
        appointment.setId(1L);
        appointment.setVisitDate(LocalDate.of(2025, 1, 31));
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDiagnoses(new HashSet<>(Set.of(diagnose)));

        sickLeave = new SickLeave();
        sickLeave.setId(1L);
        sickLeave.setStartDate(LocalDate.of(2025, 2, 1));
        sickLeave.setEndDate(LocalDate.of(2025, 2, 10));
        sickLeave.setDoctorAppointment(appointment);
        appointment.setSickLeave(sickLeave);

        treatment = new Treatment();
        treatment.setId(1L);
        treatment.setMedicineName("Paracetamol");
        treatment.setDosageAmount("1 pill");
        treatment.setFrequency("Twice a day");
        treatment.setAppointments(new HashSet<>(Set.of(appointment)));

        appointmentDto = new AppointmentDto();
        appointmentDto.setId(1L);
        appointmentDto.setVisitDate(LocalDate.of(2025, 1, 31));
        appointmentDto.setDoctorId(doctor.getId());
        appointmentDto.setPatientId(patient.getId());
        appointmentDto.setDiagnoses(new HashSet<>(Set.of(diagnose.getId())));
        appointmentDto.setSickLeaveId(sickLeave.getId());
    }

    @Test
    void testGetAllDoctors() throws Exception {
        when(doctorService.getAllDoctors()).thenReturn(Set.of(doctorDto));

        mockMvc.perform(get("/doctors/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDoctorById() throws Exception {
        Long id = 1L;
        when(doctorService.getDoctorById(id)).thenReturn(new DoctorDto());

        mockMvc.perform(get("/doctors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateDoctor() throws Exception {
        DoctorDto doctorDto = new DoctorDto();
        when(doctorService.createDoctor(any())).thenReturn(doctorDto);

        mockMvc.perform(post("/doctors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateDoctor() throws Exception {
        Long id = 1L;
        DoctorDto doctorDto = new DoctorDto();
        when(doctorService.updateDoctor(eq(id), any())).thenReturn(doctorDto);

        mockMvc.perform(put("/doctors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteDoctor() throws Exception {
        doNothing().when(doctorService).deleteDoctor(anyLong());

        mockMvc.perform(delete("/doctors/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllDoctorsWithAppointmentCount() throws Exception {
        when(doctorService.getAllDoctorsWithAppointmentCount()).thenReturn(Set.of(new DoctorAppointmentsCountDto()));

        mockMvc.perform(get("/doctors/appointments-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDoctorWithAppointmentCount() throws Exception {
        Long id = 1L;
        when(doctorService.getDoctorWithAppointmentCount(id)).thenReturn(new DoctorAppointmentsCountDto());

        mockMvc.perform(get("/doctors/{id}/appointments-count", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDoctorsWithMostSickLeaves() throws Exception {
        when(doctorService.findDoctorsWithMostSickLeaves()).thenReturn(Set.of(new DoctorDto()));

        mockMvc.perform(get("/doctors/doctors-with-most-sick-leaves")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
