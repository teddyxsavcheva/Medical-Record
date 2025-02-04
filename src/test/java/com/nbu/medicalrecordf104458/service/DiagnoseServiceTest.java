package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.DiagnoseDto;
import com.nbu.medicalrecordf104458.mapper.DiagnoseMapper;
import com.nbu.medicalrecordf104458.model.*;
import com.nbu.medicalrecordf104458.repository.*;
import com.nbu.medicalrecordf104458.service.implementation.DiagnoseServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiagnoseServiceTest {

    @Mock
    private DiagnoseRepository diagnoseRepository;

    @Mock
    private DoctorAppointmentRepository appointmentRepository;

    @Mock
    private DiagnoseMapper diagnoseMapper;

    @InjectMocks
    private DiagnoseServiceImpl diagnoseService;

    private Diagnose diagnose;
    private DiagnoseDto diagnoseDto;
    private DoctorAppointment appointment;
    private Specialization specialization;
    private Doctor doctor;
    private GeneralPractitioner gp;
    private Patient patient;

    @BeforeEach
    void setUp() {
        diagnose = new Diagnose();
        diagnose.setId(1L);
        diagnose.setName("Flu");
        diagnose.setDescription("A contagious respiratory illness.");
        diagnose.setDeleted(false);
        diagnose.setAppointments(new HashSet<>());

        diagnoseDto = new DiagnoseDto();
        diagnoseDto.setName("Flu");
        diagnoseDto.setDescription("A contagious respiratory illness.");

        specialization = new Specialization();
        specialization.setId(1L);
        specialization.setName("Cardiology");

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Doctorov");
        doctor.setSpecializations(Set.of(specialization));

        gp = new GeneralPractitioner();
        gp.setId(1L);
        gp.setName("Dr. Lekar");
        gp.setSpecializations(Set.of(specialization));

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
    }

    @Test
    public void diagnoseService_getAllDiagnoses_returnsAllDiagnoses() {
        when(diagnoseRepository.findAllByDeletedFalse()).thenReturn(Set.of(diagnose));
        when(diagnoseMapper.convertToDto(any(Diagnose.class))).thenReturn(diagnoseDto);

        Set<DiagnoseDto> result = diagnoseService.getAllDiagnoses();

        assertEquals(1, result.size());
        verify(diagnoseRepository, times(1)).findAllByDeletedFalse();
        verify(diagnoseMapper, times(1)).convertToDto(any(Diagnose.class));
    }

    @Test
    public void diagnoseService_getDiagnoseById_returnsDiagnose() {
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));
        when(diagnoseMapper.convertToDto(any(Diagnose.class))).thenReturn(diagnoseDto);

        DiagnoseDto result = diagnoseService.getDiagnoseById(1L);

        assertNotNull(result);
        assertEquals("Flu", result.getName());
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(diagnoseMapper, times(1)).convertToDto(any(Diagnose.class));
    }

    @Test
    public void diagnoseService_getDiagnoseById_throwsException() {
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> diagnoseService.getDiagnoseById(1L));
        verify(diagnoseRepository, times(1)).findById(1L);
    }

    @Test
    public void diagnoseService_createDiagnose_returnsDiagnose() {
        when(diagnoseMapper.convertToEntity(any(DiagnoseDto.class))).thenReturn(diagnose);
        when(diagnoseRepository.save(any(Diagnose.class))).thenReturn(diagnose);
        when(diagnoseMapper.convertToDto(any(Diagnose.class))).thenReturn(diagnoseDto);

        DiagnoseDto result = diagnoseService.createDiagnose(diagnoseDto);

        assertNotNull(result);
        assertEquals("Flu", result.getName());
        verify(diagnoseMapper, times(1)).convertToEntity(any(DiagnoseDto.class));
        verify(diagnoseRepository, times(1)).save(any(Diagnose.class));
        verify(diagnoseMapper, times(1)).convertToDto(any(Diagnose.class));
    }

    @Test
    void diagnoseService_updateDiagnose_returnsDiagnoseDto() {
        DiagnoseDto updatedDiagnoseDto = new DiagnoseDto(1L, "Updated Flu", "Updated seasonal flu description", new HashSet<>(Set.of(1L)));

        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(diagnoseRepository.save(any(Diagnose.class))).thenReturn(diagnose);
        when(diagnoseMapper.convertToDto(any(Diagnose.class))).thenReturn(updatedDiagnoseDto);

        DiagnoseDto result = diagnoseService.updateDiagnose(1L, updatedDiagnoseDto);

        assertEquals("Updated Flu", result.getName());
        assertEquals("Updated seasonal flu description", result.getDescription());
        assertEquals(Set.of(1L), result.getAppointmentIds());
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(1)).save(diagnose);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(diagnoseMapper, times(1)).convertToDto(any(Diagnose.class));
    }

    @Test
    void diagnoseService_updateDiagnose_returnsEntityNotFound() {
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.empty());

        DiagnoseDto diagnoseDto = new DiagnoseDto();
        diagnoseDto.setName("Updated Flu");
        diagnoseDto.setDescription("Updated seasonal flu description");

        assertThrows(EntityNotFoundException.class, () -> diagnoseService.updateDiagnose(1L, diagnoseDto));

        verify(diagnoseRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(0)).save(any(Diagnose.class));
    }

    @Test
    void diagnoseService_updateDiagnose_InvalidAppointment() {
        Diagnose existingDiagnose = new Diagnose();
        existingDiagnose.setId(1L);
        existingDiagnose.setName("Flu");
        existingDiagnose.setDescription("Seasonal flu");
        existingDiagnose.setDeleted(false);

        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(existingDiagnose));

        DiagnoseDto diagnoseDto = new DiagnoseDto();
        diagnoseDto.setName("Updated Flu");
        diagnoseDto.setDescription("Updated seasonal flu description");
        diagnoseDto.setAppointmentIds(Set.of(999L));

        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> diagnoseService.updateDiagnose(1L, diagnoseDto));

        verify(diagnoseRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).findById(999L);
        verify(diagnoseRepository, times(0)).save(any(Diagnose.class));
    }


    @Test
    void diagnoseService_deleteDiagnose_returnsVoid() {
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));

        diagnoseService.deleteDiagnose(1L);

        assertTrue(diagnose.isDeleted(), "Diagnose should be marked as deleted");
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(1)).save(diagnose);
    }

    @Test
    void diagnoseService_deleteDiagnose_returnsEntityNotFound() {
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> diagnoseService.deleteDiagnose(1L));
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(0)).save(any(Diagnose.class));
    }


    @Test
    public void diagnoseService_addDiagnoseToAppointment_returnsDiagnose() {
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(diagnoseRepository.save(any(Diagnose.class))).thenReturn(diagnose);
        when(diagnoseMapper.convertToDto(any(Diagnose.class))).thenReturn(diagnoseDto);

        DiagnoseDto result = diagnoseService.addAppointment(1L, 1L);

        assertNotNull(result);
        assertTrue(diagnose.getAppointments().contains(appointment));
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(1)).save(any(Diagnose.class));
        verify(diagnoseMapper, times(1)).convertToDto(any(Diagnose.class));
    }

    @Test
    public void diagnoseService_removeDiagnoseFromAppointment_returnsDiagnose() {
        diagnose.getAppointments().add(appointment);
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(diagnoseRepository.save(any(Diagnose.class))).thenReturn(diagnose);
        when(diagnoseMapper.convertToDto(any(Diagnose.class))).thenReturn(diagnoseDto);

        DiagnoseDto result = diagnoseService.removeAppointment(1L, 1L);

        assertNotNull(result);
        assertFalse(diagnose.getAppointments().contains(appointment));
        verify(diagnoseRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).findById(1L);
        verify(diagnoseRepository, times(1)).save(any(Diagnose.class));
        verify(diagnoseMapper, times(1)).convertToDto(any(Diagnose.class));
    }

    @Test
    void diagnoseService_findMostCommonDiagnoses_returnsDiagnoseDto() {
        Diagnose allergyDiagnose = new Diagnose();
        allergyDiagnose.setId(2L);
        allergyDiagnose.setName("Allergy");
        allergyDiagnose.setDeleted(false);
        allergyDiagnose.setDescription("An immune system reaction to substances (allergens) like pollen, dust, " +
                "or certain foods, which can cause symptoms ranging from sneezing and itching to more severe reactions such as anaphylaxis.");

        DoctorAppointment appointment1 = new DoctorAppointment();
        appointment1.setId(2L);
        appointment1.setVisitDate(LocalDate.of(2025, 1, 31));
        appointment1.setDoctor(doctor);
        appointment1.setPatient(patient);
        appointment1.setDiagnoses(new HashSet<>(Set.of(diagnose)));

        diagnose.setAppointments(new HashSet<>(Set.of(appointment1)));
        allergyDiagnose.setAppointments(new HashSet<>());

        Set<Diagnose> mockDiagnoses = Set.of(diagnose, allergyDiagnose);

        when(diagnoseRepository.findAllByDeletedFalse()).thenReturn(mockDiagnoses);

        Set<DiagnoseDto> result = diagnoseService.findMostCommonDiagnoses();

        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(diagnose.getId())));

        verify(diagnoseRepository, times(1)).findAllByDeletedFalse();
    }

}
