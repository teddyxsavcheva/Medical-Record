package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.SickLeaveDto;
import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.SickLeaveAlreadyExistsException;
import com.nbu.medicalrecordf104458.mapper.SickLeaveMapper;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.repository.SickLeaveRepository;
import com.nbu.medicalrecordf104458.service.implementation.SickLeaveServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SickLeaveServiceTest {

    @Mock
    private SickLeaveMapper sickLeaveMapper;

    @Mock
    private SickLeaveRepository sickLeaveRepository;

    @Mock
    private DoctorAppointmentRepository appointmentRepository;

    @InjectMocks
    private SickLeaveServiceImpl sickLeaveService;

    private Doctor doctor;
    private SickLeaveDto sickLeaveDto;
    private Diagnose diagnose;
    private DoctorAppointment appointment;
    private Specialization specialization;
    private GeneralPractitioner gp;
    private Patient patient;
    private SickLeave sickLeave;

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
        appointment.setDiagnoses(new HashSet<>(Set.of(diagnose)));

        sickLeave = new SickLeave();
        sickLeave.setId(1L);
        sickLeave.setStartDate(LocalDate.of(2025, 2, 1));
        sickLeave.setEndDate(LocalDate.of(2025, 2, 10));
        sickLeave.setDoctorAppointment(appointment);
        appointment.setSickLeave(sickLeave);

        sickLeaveDto = new SickLeaveDto();
        sickLeaveDto.setId(1L);
        sickLeaveDto.setStartDate(LocalDate.of(2025, 2, 1));
        sickLeaveDto.setEndDate(LocalDate.of(2025, 2, 10));
        sickLeaveDto.setDoctorAppointmentId(appointment.getId());

    }

    @Test
    void sickLeaveService_getSickLeaveById_returnsSickLeaveDto() {
        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));
        when(sickLeaveMapper.convertToDto(any(SickLeave.class))).thenReturn(sickLeaveDto);

        SickLeaveDto result = sickLeaveService.getSickLeaveById(1L);

        assertNotNull(result);
        assertEquals(sickLeaveDto, result);
    }

    @Test
    void sickLeaveService_getSickLeaveById_throwsEntityNotFound() {
        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            sickLeaveService.getSickLeaveById(1L);
        });

        assertEquals("No sick leave found with id: 1", exception.getMessage());
    }

    @Test
    void sickLeaveService_createSickLeave_returnsSickLeaveDto() {
        appointment.setSickLeave(null);

        when(sickLeaveMapper.convertToEntity(sickLeaveDto)).thenReturn(sickLeave);
        when(sickLeaveRepository.save(sickLeave)).thenReturn(sickLeave);
        when(sickLeaveMapper.convertToDto(sickLeave)).thenReturn(sickLeaveDto);

        SickLeaveDto result = sickLeaveService.createSickLeave(sickLeaveDto);

        assertNotNull(result);
        assertEquals(sickLeaveDto, result);
    }

    @Test
    void sickLeaveService_createSickLeave_throwsSickLeaveAlreadyExistsException() {
        when(sickLeaveMapper.convertToEntity(sickLeaveDto)).thenReturn(sickLeave);

        SickLeaveAlreadyExistsException exception = assertThrows(SickLeaveAlreadyExistsException.class, () -> sickLeaveService.createSickLeave(sickLeaveDto));

        assertEquals("The appointment already has a sick leave associated with it.", exception.getMessage());
    }

    @Test
    void sickLeaveService_updateSickLeave_returnsSickLeaveDto() {
        SickLeaveDto updateDto = new SickLeaveDto();
        updateDto.setId(1L);
        updateDto.setStartDate(LocalDate.of(2024, 2, 1));
        updateDto.setEndDate(LocalDate.of(2024, 2, 10));
        updateDto.setDoctorAppointmentId(appointment.getId());

        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(sickLeaveRepository.save(sickLeave)).thenReturn(sickLeave);
        when(sickLeaveMapper.convertToDto(sickLeave)).thenReturn(updateDto);

        SickLeaveDto result = sickLeaveService.updateSickLeave(1L, updateDto);

        assertNotNull(result);
        assertEquals(result.getStartDate(), updateDto.getStartDate());
    }

    @Test
    void sickLeaveService_updateSickLeave_throwsEntityNotFound() {
        SickLeaveDto updateDto = new SickLeaveDto();
        updateDto.setId(1L);
        updateDto.setStartDate(LocalDate.of(2024, 2, 1));
        updateDto.setEndDate(LocalDate.of(2024, 2, 10));
        updateDto.setDoctorAppointmentId(appointment.getId());

        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            sickLeaveService.updateSickLeave(1L, updateDto);
        });

        assertEquals("No sick leave found with id: 1", exception.getMessage());
    }

    @Test
    void sickLeaveService_deleteSickLeave_returnsVoid() {
        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));

        sickLeaveService.deleteSickLeave(1L);

        verify(sickLeaveRepository).delete(sickLeave);
    }

    @Test
    void sickLeaveService_getMonthWithMostSickLeaves_returnsSickLeaveDto() {
        when(sickLeaveRepository.findAll()).thenReturn(List.of(sickLeave));

        String result = sickLeaveService.getMonthWithMostSickLeaves();

        String expectedMonth = Month.FEBRUARY.getDisplayName(TextStyle.FULL, Locale.getDefault());

        assertTrue(result.contains(expectedMonth));
        assertTrue(result.contains("2025"));
        assertTrue(result.contains("1 sick leaves"));
    }

}
