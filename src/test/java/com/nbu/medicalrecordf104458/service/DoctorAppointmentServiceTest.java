package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.mapper.DoctorAppointmentMapper;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.model.Treatment;
import com.nbu.medicalrecordf104458.repository.DiagnoseRepository;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.repository.SickLeaveRepository;
import com.nbu.medicalrecordf104458.repository.TreatmentRepository;
import com.nbu.medicalrecordf104458.service.implementation.DoctorAppointmentServiceImpl;
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
public class DoctorAppointmentServiceTest {

    @Mock
    private DoctorAppointmentMapper appointmentMapper;

    @Mock
    private DoctorAppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DiagnoseRepository diagnoseRepository;

    @Mock
    private SickLeaveRepository sickLeaveRepository;

    @Mock
    private TreatmentRepository treatmentRepository;


    @InjectMocks
    private DoctorAppointmentServiceImpl appointmentService;

    private Doctor doctor;
    private AppointmentDto appointmentDto;
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

        appointmentDto = new AppointmentDto();
        appointmentDto.setId(1L);
        appointmentDto.setVisitDate(LocalDate.of(2025, 1, 31));
        appointmentDto.setDoctorId(doctor.getId());
        appointmentDto.setPatientId(patient.getId());
        appointmentDto.setDiagnoses(new HashSet<>(Set.of(diagnose.getId())));
        appointmentDto.setSickLeaveId(sickLeave.getId());
    }

    @Test
    public void doctorAppointmentService_getAllAppointments_returnsAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(appointmentMapper.convertToDto(any(DoctorAppointment.class))).thenReturn(appointmentDto);

        Set<AppointmentDto> result = appointmentService.getAllAppointments();

        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findAll();
        verify(appointmentMapper, times(1)).convertToDto(any(DoctorAppointment.class));
    }

    @Test
    void doctorAppointmentService_getAppointmentById_returnsDoctorDto() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.convertToDto(any(DoctorAppointment.class))).thenReturn(appointmentDto);

        AppointmentDto result = appointmentService.getAppointmentById(1L);

        assertNotNull(result);
        assertEquals(appointment.getId(), result.getId());
    }

    @Test
    void doctorAppointmentService_getAppointmentById_throwsEntityNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            appointmentService.getAppointmentById(1L);
        });

        assertEquals("No Appointment found with id: 1", exception.getMessage());
    }

    @Test
    void doctorAppointmentService_createAppointment_returnsDoctorDto() {
        when(appointmentMapper.convertToEntity(appointmentDto)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.convertToDto(appointment)).thenReturn(appointmentDto);

        AppointmentDto result = appointmentService.createAppointment(appointmentDto);

        assertNotNull(result);
        assertEquals(appointment.getId(), result.getId());
    }

    @Test
    void doctorAppointmentService_createAppointment_throwsIllegalArgumentException() {
        doctor.setDeleted(true);
        patient.setDeleted(true);

        when(appointmentMapper.convertToEntity(appointmentDto)).thenReturn(appointment);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.createAppointment(appointmentDto);
        });

        assertEquals("You can't use records that are marked for deletion!", exception.getMessage());
    }

    @Test
    void doctorAppointmentService_updateAppointment_returnsDoctorDto() {
        AppointmentDto newAppointmentDto = new AppointmentDto();
        newAppointmentDto.setId(1L);
        newAppointmentDto.setVisitDate(LocalDate.of(2025, 1, 31));
        newAppointmentDto.setDoctorId(doctor.getId());
        newAppointmentDto.setPatientId(patient.getId());
        newAppointmentDto.setDiagnoses(new HashSet<>(Set.of(diagnose.getId())));
        newAppointmentDto.setSickLeaveId(sickLeave.getId());

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(diagnoseRepository.findById(1L)).thenReturn(Optional.of(diagnose));
        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeave));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.convertToDto(appointment)).thenReturn(newAppointmentDto);

        AppointmentDto result = appointmentService.updateAppointment(1L, newAppointmentDto);

        assertNotNull(result);
        assertEquals((LocalDate.of(2025, 1, 31)), result.getVisitDate());
    }

    @Test
    void doctorAppointmentService_updateAppointment_throwsEntityNotFound() {
        AppointmentDto newAppointmentDto = new AppointmentDto();
        newAppointmentDto.setId(1L);
        newAppointmentDto.setVisitDate(LocalDate.of(2025, 1, 31));
        newAppointmentDto.setDoctorId(doctor.getId());
        newAppointmentDto.setPatientId(patient.getId());
        newAppointmentDto.setDiagnoses(new HashSet<>(Set.of(diagnose.getId())));
        newAppointmentDto.setSickLeaveId(sickLeave.getId());
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            appointmentService.updateAppointment(1L, newAppointmentDto);
        });

        assertEquals("No Appointment found with id: 1", exception.getMessage());
    }

    @Test
    void doctorAppointmentService_deleteAppointment_returnsVoid() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void doctorAppointmentService_addDiagnose_returnsAppointmentDto() {
        Diagnose diagnose1 = new Diagnose();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(diagnoseRepository.findById(2L)).thenReturn(Optional.of(diagnose1));

        appointmentService.addDiagnose(1L, 2L);

        verify(appointmentRepository).save(appointment);
        assertTrue(appointment.getDiagnoses().contains(diagnose1));
    }

    @Test
    void doctorAppointmentService_addDiagnose_throwsEntityNotFoundException() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(diagnoseRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            appointmentService.addDiagnose(1L, 2L);
        });

        assertEquals("No Diagnose found with id: 2", exception.getMessage());
    }

    @Test
    void doctorAppointmentService_removeDiagnose_returnsAppointmentDto() {
        Diagnose diagnose1 = new Diagnose();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(diagnoseRepository.findById(2L)).thenReturn(Optional.of(diagnose1));

        appointmentService.removeDiagnose(1L, 2L);

        verify(appointmentRepository).save(appointment);
        assertFalse(appointment.getDiagnoses().contains(diagnose1));
    }

    @Test
    void doctorAppointmentService_removeDiagnose_throwsEntityNotFoundException() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(diagnoseRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            appointmentService.removeDiagnose(1L, 2L);
        });

        assertEquals("No Diagnose found with id: 2", exception.getMessage());
    }

    @Test
    void doctorAppointmentService_addTreatment_returnsAppointmentDto() {
        Treatment treatment1 = new Treatment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(treatmentRepository.findById(2L)).thenReturn(Optional.of(treatment1));

        appointmentService.addTreatment(1L, 2L);

        verify(appointmentRepository).save(appointment);
        assertTrue(appointment.getTreatments().contains(treatment1));
    }

    @Test
    void doctorAppointmentService_addTreatment_throwsEntityNotFoundException() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(treatmentRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            appointmentService.addTreatment(1L, 2L);
        });

        assertEquals("No Treatment found with id: 2", exception.getMessage());

    }

    @Test
    void doctorAppointmentService_removeTreatment_returnsAppointmentDto() {
        Treatment treatment1 = new Treatment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(treatmentRepository.findById(2L)).thenReturn(Optional.of(treatment1));

        appointmentService.removeTreatment(1L, 2L);

        verify(appointmentRepository).save(appointment);
        assertFalse(appointment.getTreatments().contains(treatment1));
    }

    @Test
    void doctorAppointmentService_removeTreatment_throwsEntityNotFoundException() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(treatmentRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            appointmentService.removeTreatment(1L, 2L);
        });

        assertEquals("No Treatment found with id: 2", exception.getMessage());
    }


    @Test
    void doctorAppointmentService_findVisitsByDateRange_returnsAppointmentDtos() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 3);

        AppointmentDto newAppointmentDto = new AppointmentDto();
        newAppointmentDto.setId(1L);
        newAppointmentDto.setVisitDate(LocalDate.of(2025, 2, 4));
        newAppointmentDto.setDoctorId(doctor.getId());
        newAppointmentDto.setPatientId(patient.getId());
        newAppointmentDto.setDiagnoses(new HashSet<>(Set.of(diagnose.getId())));
        newAppointmentDto.setSickLeaveId(sickLeave.getId());

        Set<AppointmentDto> expectedAppointmentsDtos = Set.of(appointmentDto);
        Set<DoctorAppointment> expectedAppointments = Set.of(appointment);

        when(appointmentRepository.findVisitsByDateRange(startDate, endDate)).thenReturn(expectedAppointments);
        when(appointmentMapper.convertToDto(any(DoctorAppointment.class))).thenReturn(appointmentDto);

        Set<AppointmentDto> result = appointmentService.findVisitsByDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(expectedAppointments.size(), result.size());
        assertEquals(expectedAppointmentsDtos, result);
    }

    @Test
    void doctorAppointmentService_findAppointmentsByDoctorAndDateRange_returnsAppointmentDtos() {
        Long doctorId = 1L;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 3);

        AppointmentDto newAppointmentDto = new AppointmentDto();
        newAppointmentDto.setId(1L);
        newAppointmentDto.setVisitDate(LocalDate.of(2025, 2, 4));
        newAppointmentDto.setDoctorId(doctor.getId());
        newAppointmentDto.setPatientId(patient.getId());
        newAppointmentDto.setDiagnoses(new HashSet<>(Set.of(diagnose.getId())));
        newAppointmentDto.setSickLeaveId(sickLeave.getId());

        Set<DoctorAppointment> expectedAppointments = Set.of(appointment);
        Set<AppointmentDto> expectedAppointmentsDtos = Set.of(appointmentDto);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findAppointmentsByDoctorAndDateRange(1L, startDate, endDate)).thenReturn(expectedAppointments);
        when(appointmentMapper.convertToDto(any(DoctorAppointment.class))).thenReturn(appointmentDto);

        Set<AppointmentDto> result = appointmentService.findAppointmentsByDoctorAndDateRange(doctorId, startDate, endDate);

        assertNotNull(result);
        assertEquals(expectedAppointments.size(), result.size());
        assertEquals(expectedAppointmentsDtos, result);
    }

}
