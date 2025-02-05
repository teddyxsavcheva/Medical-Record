package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.dto.queries.DoctorAppointmentsCountDto;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import com.nbu.medicalrecordf104458.mapper.DoctorMapper;
import com.nbu.medicalrecordf104458.service.implementation.DoctorServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctor;
    private DoctorDto doctorDto;
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
        appointment.setDiagnoses(new HashSet<>());

        sickLeave = new SickLeave();
        sickLeave.setId(1L);
        sickLeave.setStartDate(LocalDate.of(2025, 2, 1));
        sickLeave.setEndDate(LocalDate.of(2025, 2, 10));
        sickLeave.setDoctorAppointment(appointment);
        appointment.setSickLeave(sickLeave);
    }

    @Test
    void doctorService_getDoctorById_returnsDoctorDto() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorMapper.convertToDto(any(Doctor.class))).thenReturn(doctorDto);

        DoctorDto result = doctorService.getDoctorById(1L);

        assertNotNull(result);
        assertEquals(doctorDto.getName(), result.getName());
    }

    @Test
    void doctorService_getDoctorById_throwsEntityNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            doctorService.getDoctorById(1L);
        });

        assertEquals("No doctor found with id: 1", exception.getMessage());
    }

    @Test
    void doctorService_createDoctor_returnsDoctorDto() {
        when(doctorMapper.convertToEntity(doctorDto)).thenReturn(doctor);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(doctorMapper.convertToDto(doctor)).thenReturn(doctorDto);

        DoctorDto result = doctorService.createDoctor(doctorDto);

        assertNotNull(result);
        assertEquals(doctorDto.getName(), result.getName());
    }

    @Test
    void doctorService_createDoctor_throwsIllegalArgumentException() {
        specialization.setDeleted(true);
        doctor.setSpecializations(Set.of(specialization));
        when(doctorMapper.convertToEntity(doctorDto)).thenReturn(doctor);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            doctorService.createDoctor(doctorDto);
        });

        assertEquals("You can't use records that are marked for deletion!", exception.getMessage());
    }

    @Test
    void doctorService_updateDoctor_returnsDoctorDto() {
        DoctorDto updateDto = new DoctorDto(1L, "Dr. John Updated", Set.of(specialization.getId()));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(doctorMapper.convertToDto(doctor)).thenReturn(updateDto);

        DoctorDto result = doctorService.updateDoctor(1L, updateDto);

        assertNotNull(result);
        assertEquals("Dr. John Updated", result.getName());
    }

    @Test
    void doctorService_updateDoctor_throwsEntityNotFound() {
        DoctorDto updateDto = new DoctorDto(1L, "Dr. John Updated", Set.of());
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            doctorService.updateDoctor(1L, updateDto);
        });

        assertEquals("No doctor found with id: 1", exception.getMessage());
    }

    @Test
    void doctorService_deleteDoctor_returnsVoid() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        doctorService.deleteDoctor(1L);

        verify(doctorRepository).save(doctor);
    }

    @Test
    void doctorService_deleteDoctor_throwsEntityNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            doctorService.deleteDoctor(1L);
        });

        assertEquals("No doctor found with id: 1", exception.getMessage());
    }

    @Test
    void doctorService_addSpecialization_returnsDoctorDto() {
        Specialization specialization = new Specialization();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));

        doctorService.addSpecialization(1L, 1L);

        verify(doctorRepository).save(doctor);
        assertTrue(doctor.getSpecializations().contains(specialization));
    }

    @Test
    void doctorService_addSpecialization_throwsEntityNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(specializationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            doctorService.addSpecialization(1L, 1L);
        });

        assertEquals("No specialization found with id: 1", exception.getMessage());
    }

    @Test
    void doctorService_removeSpecialization_returnsDoctorDto() {
        Specialization specializationToRemove = new Specialization();
        specializationToRemove.setId(2L);
        specializationToRemove.setName("Bla bla");
        specializationToRemove.setDeleted(false);
        specializationToRemove.setDoctors(new HashSet<>());

        doctor.getSpecializations().add(specializationToRemove);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(specializationRepository.findById(2L)).thenReturn(Optional.of(specializationToRemove));

       doctorService.removeSpecialization(1L, 2L);

        verify(doctorRepository).save(doctor);
        assertFalse(doctor.getSpecializations().contains(specializationToRemove));
    }

    @Test
    void doctorService_removeSpecialization_throwsEntityNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(specializationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            doctorService.removeSpecialization(1L, 1L);
        });

        assertEquals("No specialization found with id: 1", exception.getMessage());
    }

    @Test
    void doctorService_getAllDoctorsWithAppointmentCount_returnsDoctorAppointmentsCountDto() {
        DoctorAppointmentsCountDto countDto = new DoctorAppointmentsCountDto();
        countDto.setDoctorId(1L);
        countDto.setAppointmentsCount(5L);

        Set<DoctorAppointmentsCountDto> appointmentCounts = new HashSet<>();
        appointmentCounts.add(countDto);

        when(doctorRepository.findAllDoctorsWithAppointmentCount()).thenReturn(appointmentCounts);

        Set<DoctorAppointmentsCountDto> result = doctorService.getAllDoctorsWithAppointmentCount();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(5L, countDto.getAppointmentsCount());
    }

    @Test
    void doctorService_getDoctorWithAppointmentCount_returnsDoctorAppointmentsCountDto() {
        DoctorAppointmentsCountDto countDto = new DoctorAppointmentsCountDto();
        countDto.setDoctorId(1L);
        countDto.setAppointmentsCount(5L);

        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(doctorRepository.findDoctorWithAppointmentCount(1L)).thenReturn(countDto);

        DoctorAppointmentsCountDto result = doctorService.getDoctorWithAppointmentCount(1L);

        assertNotNull(result);
        assertEquals(1L, result.getDoctorId());
        assertEquals(5L, result.getAppointmentsCount());
    }

    @Test
    void doctorService_findDoctorsWithMostSickLeaves_returnsDoctorDto() {
        doctor.setDeleted(false);
        doctor.getAppointments().add(appointment);

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setName("Dr. Lekar");
        doctor2.setAppointments(new HashSet<>());
        doctor2.setDeleted(false);
        doctor2.setSpecializations(new HashSet<>(Set.of(specialization)));

        DoctorAppointment appointment2 = new DoctorAppointment();
        appointment2.setId(2L);
        appointment2.setVisitDate(LocalDate.of(2025, 2, 1));
        appointment2.setDoctor(doctor2);
        appointment2.setPatient(patient);
        appointment2.setDiagnoses(new HashSet<>());

        SickLeave sickLeave2 = new SickLeave();
        sickLeave2.setId(2L);
        sickLeave2.setStartDate(LocalDate.of(2025, 2, 1));
        sickLeave2.setEndDate(LocalDate.of(2025, 2, 5));
        sickLeave2.setDoctorAppointment(appointment2);

        appointment2.setSickLeave(sickLeave2);
        doctor2.getAppointments().add(appointment2);

        when(doctorRepository.findAllByDeletedFalse()).thenReturn(Set.of(doctor, doctor2));
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(doctorRepository.findById(doctor2.getId())).thenReturn(Optional.of(doctor2));

        Set<DoctorDto> result = doctorService.findDoctorsWithMostSickLeaves();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(d -> d.getId() == 1L));
        assertTrue(result.stream().anyMatch(d -> d.getId() == 2L));
    }


}
