package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.SpecializationDto;
import com.nbu.medicalrecordf104458.mapper.SpecializationMapper;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import com.nbu.medicalrecordf104458.service.implementation.SpecializationServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpecializationServiceTest {
    
    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private DoctorRepository doctorRepository;
    
    @Mock
    private SpecializationMapper specializationMapper;
    
    @InjectMocks
    private SpecializationServiceImpl specializationService;

    private Doctor doctor;
    private SpecializationDto specializationDto;
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

        specialization.setDoctors(new HashSet<>(Set.of(doctor, gp)));

        specializationDto = new SpecializationDto();
        specializationDto.setId(1L);
        specializationDto.setName("Cardiology");
        specializationDto.setDoctorIds(new HashSet<>(Set.of(doctor.getId(), gp.getId())));

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

    }

    @Test
    void specializationService_getSpecializationById_returnsSpecializationDto() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(specializationMapper.convertToDto(any(Specialization.class))).thenReturn(specializationDto);

        SpecializationDto result = specializationService.getSpecializationById(1L);

        assertNotNull(result);
        assertEquals(specializationDto, result);
    }

    @Test
    void specializationService_getSpecializationById_throwsEntityNotFound() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            specializationService.getSpecializationById(1L);
        });

        assertEquals("No Specialization found with id: 1", exception.getMessage());
    }

    @Test
    void specializationService_createSpecialization_returnsSpecializationDto() {
        when(specializationMapper.convertToEntity(specializationDto)).thenReturn(specialization);
        when(specializationRepository.save(specialization)).thenReturn(specialization);
        when(specializationMapper.convertToDto(specialization)).thenReturn(specializationDto);

        SpecializationDto result = specializationService.createSpecialization(specializationDto);

        assertNotNull(result);
        assertEquals(specializationDto, result);
    }

    @Test
    void specializationService_createSpecialization_throwsSpecializationAlreadyExistsException() {
        gp.setDeleted(true);
        doctor.setDeleted(true);
        when(specializationMapper.convertToEntity(specializationDto)).thenReturn(specialization);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> specializationService.createSpecialization(specializationDto));

        assertEquals("You can't use records that are marked for deletion!", exception.getMessage());
    }

    @Test
    void specializationService_updateSpecialization_returnsSpecializationDto() {
        SpecializationDto updatedDto = new SpecializationDto();
        updatedDto.setId(1L);
        updatedDto.setName("Family Doctor");
        updatedDto.setDoctorIds(new HashSet<>(Set.of(doctor.getId(), gp.getId())));

        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(specializationRepository.save(specialization)).thenReturn(specialization);
        when(specializationMapper.convertToDto(specialization)).thenReturn(updatedDto);

        SpecializationDto result = specializationService.updateSpecialization(1L, updatedDto);

        assertNotNull(result);
        assertEquals(result, updatedDto);
    }

    @Test
    void specializationService_updateSpecialization_throwsEntityNotFound() {
        SpecializationDto updateDto = new SpecializationDto();

        when(specializationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            specializationService.updateSpecialization(1L, updateDto);
        });

        assertEquals("No Specialization found with id: 1", exception.getMessage());
    }

    @Test
    void specializationService_deleteSpecialization_returnsVoid() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));

        specializationService.deleteSpecialization(1L);

        verify(specializationRepository).save(specialization);
    }

    @Test
    void specializationService_addDoctor_returnsSpecializationDto() {
        Doctor doctor3 = new Doctor();
        doctor3.setId(3L);
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(doctorRepository.findById(3L)).thenReturn(Optional.of(doctor3));

        specializationService.addDoctor(1L, 3L);

        verify(specializationRepository).save(specialization);
        assertTrue(specialization.getDoctors().contains(doctor3));
    }

    @Test
    void specializationService_addDoctor_throwsEntityNotFoundException() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(doctorRepository.findById(3L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            specializationService.addDoctor(1L, 3L);
        });

        assertEquals("No doctor found with id: 3", exception.getMessage());
    }

    @Test
    void specializationService_removeDoctor_returnsSpecializationDto() {
        Doctor doctor3 = new Doctor();
        doctor3.setId(3L);
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(doctorRepository.findById(3L)).thenReturn(Optional.of(doctor3));

        specializationService.removeDoctor(1L, 3L);

        verify(specializationRepository).save(specialization);
        assertFalse(specialization.getDoctors().contains(doctor3));
    }

    @Test
    void specializationService_removeDoctor_throwsEntityNotFoundException() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(doctorRepository.findById(3L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            specializationService.removeDoctor(1L, 3L);
        });

        assertEquals("No doctor found with id: 3", exception.getMessage());
    }

}
