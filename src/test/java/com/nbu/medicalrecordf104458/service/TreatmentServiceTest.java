package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.TreatmentDto;
import com.nbu.medicalrecordf104458.mapper.TreatmentMapper;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.model.Treatment;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.repository.TreatmentRepository;
import com.nbu.medicalrecordf104458.service.implementation.TreatmentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TreatmentServiceTest {

    @Mock
    private TreatmentMapper treatmentMapper;

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private DoctorAppointmentRepository appointmentRepository;

    @InjectMocks
    private TreatmentServiceImpl treatmentService;

    private Doctor doctor;
    private Diagnose diagnose;
    private DoctorAppointment appointment;
    private Specialization specialization;
    private GeneralPractitioner gp;
    private Patient patient;
    private SickLeave sickLeave;
    private Treatment treatment;
    private TreatmentDto treatmentDto;

    @BeforeEach
    void setUp() {
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
        appointment.setDiagnoses(new HashSet<>());

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

        treatmentDto = new TreatmentDto();
        treatmentDto.setId(1L);
        treatmentDto.setMedicineName("Paracetamol");
        treatmentDto.setDosageAmount("1 pill");
        treatmentDto.setFrequency("Twice a day");
        treatmentDto.setAppointmentIds(new HashSet<>(Set.of(appointment.getId())));
    }

    @Test
    public void treatmentService_getAllTreatments_returnsAllTreatments() {
        when(treatmentRepository.findAll()).thenReturn(List.of(treatment));
        when(treatmentMapper.convertToDto(any(Treatment.class))).thenReturn(treatmentDto);

        Set<TreatmentDto> result = treatmentService.getAllTreatments();

        assertEquals(1, result.size());
        verify(treatmentMapper, times(1)).convertToDto(any(Treatment.class));
    }

    @Test
    void treatmentService_getTreatmentById_returnsTreatmentDto() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(treatmentMapper.convertToDto(any(Treatment.class))).thenReturn(treatmentDto);

        TreatmentDto result = treatmentService.getTreatmentById(1L);

        assertNotNull(result);
        assertEquals(treatmentDto, result);
    }

    @Test
    void treatmentService_getTreatmentById_throwsEntityNotFound() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            treatmentService.getTreatmentById(1L);
        });

        assertEquals("No Treatment found with id: 1", exception.getMessage());
    }

    @Test
    void treatmentService_createTreatment_returnsTreatmentDto() {
        when(treatmentMapper.convertToEntity(treatmentDto)).thenReturn(treatment);
        when(treatmentRepository.save(treatment)).thenReturn(treatment);
        when(treatmentMapper.convertToDto(treatment)).thenReturn(treatmentDto);

        TreatmentDto result = treatmentService.createTreatment(treatmentDto);

        assertNotNull(result);
        assertEquals(treatmentDto, result);
    }

    @Test
    void treatmentService_createTreatment_throwsEntityNotFoundException() {
        when(treatmentMapper.convertToEntity(treatmentDto))
                .thenThrow(new EntityNotFoundException("No appointment found with id: 1"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            treatmentService.createTreatment(treatmentDto);
        });

        assertEquals("No appointment found with id: 1", exception.getMessage());
    }

    @Test
    void treatmentService_updateTreatment_returnsTreatmentDto() {
        TreatmentDto updateDto = new TreatmentDto();
        updateDto.setId(1L);
        updateDto.setMedicineName("Norufen");
        updateDto.setDosageAmount("1 pill");
        updateDto.setFrequency("Twice a day");
        updateDto.setAppointmentIds(new HashSet<>(Set.of(appointment.getId())));

        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(treatmentRepository.save(treatment)).thenReturn(treatment);
        when(treatmentMapper.convertToDto(treatment)).thenReturn(updateDto);

        TreatmentDto result = treatmentService.updateTreatment(1L, updateDto);

        assertNotNull(result);
        assertEquals("Norufen", result.getMedicineName());
    }

    @Test
    void treatmentService_updateTreatment_throwsEntityNotFound() {
        TreatmentDto updateDto = new TreatmentDto();
        when(treatmentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            treatmentService.updateTreatment(1L, updateDto);
        });

        assertEquals("No Treatment found with id: 1", exception.getMessage());
    }

    @Test
    void treatmentService_deleteTreatment_returnsVoid() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));

        treatmentService.deleteTreatment(1L);

        verify(treatmentRepository).delete(treatment);
    }

    @Test
    void treatmentService_deleteTreatment_throwsEntityNotFound() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            treatmentService.deleteTreatment(1L);
        });

        assertEquals("No Treatment found with id: 1", exception.getMessage());
    }

    @Test
    void treatmentService_addAppointment_returnsTreatmentDto() {
        DoctorAppointment newAppointment = new DoctorAppointment();
        newAppointment.setId(2L);
        newAppointment.setVisitDate(LocalDate.of(2025, 1, 31));
        newAppointment.setDoctor(doctor);
        newAppointment.setPatient(patient);
        newAppointment.setDiagnoses(new HashSet<>());
        newAppointment.setTreatments(new HashSet<>());

        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.findById(2L)).thenReturn(Optional.of(appointment));

        treatmentService.addAppointment(1L, 2L);

        verify(treatmentRepository).save(treatment);
        assertTrue(treatment.getAppointments().contains(appointment));
    }

    @Test
    void treatmentService_addAppointment_throwsEntityNotFound() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.findById(3L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            treatmentService.addAppointment(1L, 3L);
        });

        assertEquals("No Appointment found with id: 3", exception.getMessage());
    }

    @Test
    void treatmentService_removeAppointment_returnsTreatmentDto() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.findById(2L)).thenReturn(Optional.of(appointment));

        treatmentService.removeAppointment(1L, 2L);

        verify(treatmentRepository).save(treatment);
        assertFalse(doctor.getAppointments().contains(appointment));
    }

    @Test
    void treatmentService_removeAppointment_throwsEntityNotFound() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            treatmentService.removeAppointment(1L, 1L);
        });

        assertEquals("No Appointment found with id: 1", exception.getMessage());
    }

}
