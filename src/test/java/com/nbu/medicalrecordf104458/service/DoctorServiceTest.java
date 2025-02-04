package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
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

    // TODO:
    @Test
    void doctorService_removeSpecialization_returnsDoctorDto() {

    }

    @Test
    void doctorService_removeSpecialization_throwsEntityNotFound() {

    }

    @Test
    void doctorService_getAllDoctorsWithAppointmentCount_returnsDoctorAppointmentsCountDto() {

    }

   @Test
    void doctorService_getDoctorWithAppointmentCount_returnsDoctorAppointmentsCountDto() {

    }

   @Test
    void doctorService_findDoctorsWithMostSickLeaves_returnsDoctorDto() {

    }

}
