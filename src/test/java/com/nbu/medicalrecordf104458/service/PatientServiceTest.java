package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.PatientDto;
import com.nbu.medicalrecordf104458.mapper.DoctorAppointmentMapper;
import com.nbu.medicalrecordf104458.mapper.PatientMapper;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.DiagnoseRepository;
import com.nbu.medicalrecordf104458.repository.GeneralPractitionerRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.service.implementation.PatientServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
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
public class PatientServiceTest {

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private DoctorAppointmentMapper appointmentMapper;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DiagnoseRepository diagnoseRepository;

    @Mock
    private GeneralPractitionerRepository gpRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Doctor doctor;
    private Diagnose diagnose;
    private DoctorAppointment appointment;
    private AppointmentDto appointmentDto;
    private Specialization specialization;
    private GeneralPractitioner gp;
    private Patient patient;
    private PatientDto patientDto;
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

        gp.setPatients(new HashSet<>(Set.of(patient)));

        patientDto = new PatientDto();
        patientDto.setId(1L);
        patientDto.setName("Pacientov");
        patientDto.setFamilyDoctorId(gp.getId());
        patientDto.setLastInsurancePayment(LocalDate.of(2025, 1, 31));
        patientDto.setUnifiedCivilNumber(2L);

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

        appointmentDto = new AppointmentDto();
        appointmentDto.setId(1L);
        appointmentDto.setVisitDate(LocalDate.of(2025, 1, 31));
        appointmentDto.setDoctorId(doctor.getId());
        appointmentDto.setPatientId(patient.getId());
        appointmentDto.setDiagnoses(new HashSet<>(Set.of(diagnose.getId())));
        appointmentDto.setSickLeaveId(sickLeave.getId());
    }

    @Test
    public void patientService_getAllPatients_returnsAllPatients() {
        when(patientRepository.findAllByDeletedFalse()).thenReturn(Set.of(patient));
        when(patientMapper.convertToDto(any(Patient.class))).thenReturn(patientDto);

        Set<PatientDto> result = patientService.getAllPatients();

        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findAllByDeletedFalse();
        verify(patientMapper, times(1)).convertToDto(any(Patient.class));
    }

    @Test
    void patientService_getPatientById_returnsPatientDto() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.convertToDto(any(Patient.class))).thenReturn(patientDto);

        PatientDto result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals(patientDto, result);
    }

    @Test
    void patientService_getPatientById_throwsEntityNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.getPatientById(1L);
        });

        assertEquals("No patient found with id: 1", exception.getMessage());
    }

    @Test
    void patientService_createPatient_returnsPatientDto() {
        when(patientMapper.convertToEntity(patientDto)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.convertToDto(patient)).thenReturn(patientDto);

        PatientDto result = patientService.createPatient(patientDto);

        assertNotNull(result);
        assertEquals(patientDto, result);
    }

    @Test
    void patientService_createPatient_throwsIllegalArgumentException() {
        gp.setDeleted(true);
        when(patientMapper.convertToEntity(patientDto)).thenReturn(patient);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            patientService.createPatient(patientDto);
        });

        assertEquals("You can't use records that are marked for deletion!", exception.getMessage());
    }

    @Test
    void patientService_updatePatient_returnsPatientDto() {
        PatientDto updateDto = new PatientDto(1L, "Pacient nov", gp.getId(), LocalDate.of(2025, 1, 31), 2L);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.convertToDto(patient)).thenReturn(updateDto);

        PatientDto result = patientService.updatePatient(1L, updateDto);

        assertNotNull(result);
        assertEquals("Pacient nov", result.getName());
    }

    @Test
    void patientService_updatePatient_throwsEntityNotFound() {
        PatientDto updateDto = new PatientDto(1L, "Pacient nov", gp.getId(), LocalDate.of(2025, 1, 31), 2L);
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.updatePatient(1L, updateDto);
        });

        assertEquals("No patient found with id: 1", exception.getMessage());
    }

    @Test
    void patientService_deletePatient_returnsVoid() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        patientService.deletePatient(1L);

        verify(patientRepository).save(patient);
    }

    @Test
    void patientService_deletePatient_throwsEntityNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.deletePatient(1L);
        });

        assertEquals("No patient found with id: 1", exception.getMessage());
    }

    @Test
    void patientService_isInsurancePaidLast6Months_returnsTrue() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        boolean result = patientService.isInsurancePaidLast6Months(1L);

        assertTrue(result);
    }

    @Test
    void patientService_isInsurancePaidLast6Months_returnsFalse() {
        patient.setLastInsurancePayment(LocalDate.now().minusMonths(7));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        boolean result = patientService.isInsurancePaidLast6Months(1L);

        assertFalse(result);
    }

    @Test
    void patientService_getPatientsByDiagnoseId_returnsPatientDtos() {
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));
        when(patientRepository.findPatientsByDiagnoseId(1L)).thenReturn(Set.of(patient));
        when(patientMapper.convertToDto(patient)).thenReturn(patientDto);

        Set<PatientDto> result = patientService.getPatientsByDiagnoseId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(patientDto));
    }

    @Test
    void patientService_getPatientsByDiagnoseId_throwsEntityNotFound() {
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.getPatientsByDiagnoseId(1L);
        });

        assertEquals("No Diagnose found with id: 1", exception.getMessage());
    }

    @Test
    void patientService_getPatientsByGeneralPractitioner_returnsPatientDtos() {
        when(gpRepository.findById(3L)).thenReturn(Optional.of(gp));
        when(patientRepository.findPatientsByGeneralPractitionerId(3L)).thenReturn(Set.of(patient));
        when(patientMapper.convertToDto(patient)).thenReturn(patientDto);

        Set<PatientDto> result = patientService.getPatientsByGeneralPractitioner(3L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(patientDto));
    }

    @Test
    void patientService_getVisitsByPatient_returnsAppointmentDtos() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.findVisitsByPatientId(1L)).thenReturn(Set.of(appointment));
        when(appointmentMapper.convertToDto(appointment)).thenReturn(appointmentDto);

        Set<AppointmentDto> result = patientService.getVisitsByPatient(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(appointmentDto));
    }

}
