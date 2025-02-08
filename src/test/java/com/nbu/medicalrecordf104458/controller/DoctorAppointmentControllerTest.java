package com.nbu.medicalrecordf104458.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecordf104458.config.JwtAuthFilter;
import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.DiagnoseDto;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.model.Treatment;
import com.nbu.medicalrecordf104458.service.DoctorAppointmentService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DoctorAppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@MockBean(JwtAuthFilter.class) // Mocks out JWT filter so it's not used in tests
public class DoctorAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorAppointmentService appointmentService;

    private DiagnoseDto fluDto;
    private Doctor doctor;
    private AppointmentDto appointmentDto;
    private Diagnose diagnose;
    private DoctorAppointment appointment;
    private Specialization specialization;
    private GeneralPractitioner gp;
    private Patient patient;
    private SickLeave sickLeave;
    private Treatment treatment;
    private Set<AppointmentDto> mockAppointments;

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
    void testGetAllAppointments() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(Set.of(appointmentDto));

        mockMvc.perform(get("/doctor-appointments/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)) // Check that there's one appointment
                .andExpect(jsonPath("$[0].id").value(appointmentDto.getId()))
                .andExpect(jsonPath("$[0].visitDate").value(appointmentDto.getVisitDate().toString()))
                .andExpect(jsonPath("$[0].doctorId").value(appointmentDto.getDoctorId()))
                .andExpect(jsonPath("$[0].patientId").value(appointmentDto.getPatientId()))
                .andExpect(jsonPath("$[0].diagnoses[0]").value(diagnose.getId()))
                .andExpect(jsonPath("$[0].sickLeaveId").value(sickLeave.getId()));

    }

    @Test
    void testGetAppointmentById() throws Exception {
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointmentDto);

        mockMvc.perform(get("/doctor-appointments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentDto.getId()));
    }

    @Test
    void testCreateAppointment() throws Exception {
        when(appointmentService.createAppointment(any())).thenReturn(appointmentDto);

        mockMvc.perform(post("/doctor-appointments/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(appointmentDto.getId()));
    }

    @Test
    void testUpdateAppointment() throws Exception {
        when(appointmentService.updateAppointment(eq(1L), any())).thenReturn(appointmentDto);

        mockMvc.perform(put("/doctor-appointments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentDto.getId()));
    }

    @Test
    void testDeleteAppointment() throws Exception {
        doNothing().when(appointmentService).deleteAppointment(1L);

        mockMvc.perform(delete("/doctor-appointments/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testAddDiagnose() throws Exception {
        when(appointmentService.addDiagnose(1L, 1L)).thenReturn(appointmentDto);

        mockMvc.perform(post("/doctor-appointments/1/diagnoses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentDto.getId()));
    }

    @Test
    void testRemoveDiagnose() throws Exception {
        when(appointmentService.removeDiagnose(1L, 1L)).thenReturn(appointmentDto);

        mockMvc.perform(delete("/doctor-appointments/1/diagnoses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentDto.getId()));
    }

    @Test
    void testAddTreatment() throws Exception {
        when(appointmentService.addTreatment(1L, 1L)).thenReturn(appointmentDto);

        mockMvc.perform(post("/doctor-appointments/1/treatments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentDto.getId()));
    }

    @Test
    void testRemoveTreatment() throws Exception {
        when(appointmentService.removeTreatment(1L, 1L)).thenReturn(appointmentDto);

        mockMvc.perform(delete("/doctor-appointments/1/treatments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentDto.getId()));
    }

    @Test
    void testGetAppointmentsBetweenDates() throws Exception {
        when(appointmentService.findVisitsByDateRange(any(), any())).thenReturn(Set.of(appointmentDto));

        mockMvc.perform(get("/doctor-appointments/between-dates/{startDate}/{endDate}", "2025-01-01", "2025-02-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(appointmentDto.getId()));
    }

    @Test
    void testGetAppointmentsByDoctorAndDateRange() throws Exception {
        when(appointmentService.findAppointmentsByDoctorAndDateRange(1L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)))
                .thenReturn(Set.of(appointmentDto));

        mockMvc.perform(get("/doctor-appointments/doctor-and-between-dates/1/2025-01-01/2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(appointmentDto.getId()));
    }

}
