package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.dto.GeneralPractitionerDto;
import com.nbu.medicalrecordf104458.dto.queries.GpPatientsCountDto;
import com.nbu.medicalrecordf104458.mapper.GeneralPractitionerMapper;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.GeneralPractitionerRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import com.nbu.medicalrecordf104458.service.implementation.GeneralPractitionerServiceImpl;
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
public class GeneralPractitionerServiceTest {

    @Mock
    private GeneralPractitionerMapper gpMapper;

    @Mock
    private GeneralPractitionerRepository gpRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private GeneralPractitionerServiceImpl gpService;

    private Doctor doctor;
    private DoctorDto doctorDto;
    private GeneralPractitionerDto gpDto;
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

        patient = new Patient();
        patient.setId(1L);
        patient.setName("Pacientov");
        patient.setFamilyDoctor(gp);
        patient.setLastInsurancePayment(LocalDate.of(2025, 1, 31));
        patient.setUnifiedCivilNumber(1234L);

        gp = new GeneralPractitioner();
        gp.setId(2L);
        gp.setName("Dr. Lekar");
        gp.setSpecializations(new HashSet<>(Set.of(specialization)));
        gp.setPatients(new HashSet<>(Set.of(patient)));

        gpDto = new GeneralPractitionerDto();
        gpDto.setDoctor(doctorDto);
        gpDto.setPatients(new HashSet<>(Set.of(patient.getId())));

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
    void generalPractitionerService_getDoctorById_returnsGpDto() {
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(gpMapper.convertToDto(any(GeneralPractitioner.class))).thenReturn(gpDto);

        GeneralPractitionerDto result = gpService.getDoctorById(2L);

        assertNotNull(result);
        assertEquals(gpDto.getPatients(), result.getPatients());
    }

    @Test
    void generalPractitionerService_getDoctorById_throwsEntityNotFound() {
        when(gpRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gpService.getDoctorById(2L);
        });

        assertEquals("No GP found with id: 2", exception.getMessage());
    }

    @Test
    void generalPractitionerService_createDoctor_returnsGpDto() {
        when(gpMapper.convertToEntity(gpDto)).thenReturn(gp);
        when(gpRepository.save(gp)).thenReturn(gp);
        when(gpMapper.convertToDto(gp)).thenReturn(gpDto);

        GeneralPractitionerDto result = gpService.createDoctor(gpDto);

        assertNotNull(result);
        assertEquals(gpDto.getPatients(), result.getPatients());
    }

    @Test
    void generalPractitionerService_createDoctor_throwsIllegalArgumentException() {
        specialization.setDeleted(true);
        when(gpMapper.convertToEntity(gpDto)).thenReturn(gp);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gpService.createDoctor(gpDto);
        });

        assertEquals("You can't use records that are marked for deletion!", exception.getMessage());
    }

    @Test
    void generalPractitionerService_updateDoctor_returnsGpDto() {
        DoctorDto newDoctorDto = new DoctorDto();
        newDoctorDto.setId(1L);
        newDoctorDto.setName("Dr. John Updated");
        newDoctorDto.setSpecializationIds(new HashSet<>(Set.of(specialization.getId())));

        GeneralPractitionerDto updateGpDto = new GeneralPractitionerDto(newDoctorDto, Set.of(patient.getId()));

        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(gpRepository.save(gp)).thenReturn(gp);
        when(gpMapper.convertToDto(gp)).thenReturn(updateGpDto);

        GeneralPractitionerDto result = gpService.updateDoctor(2L, updateGpDto);

        assertNotNull(result);
        assertEquals("Dr. John Updated", result.getDoctor().getName());
    }

    @Test
    void generalPractitionerService_updateDoctor_throwsEntityNotFound() {
        DoctorDto newDoctorDto = new DoctorDto();
        newDoctorDto.setId(1L);
        newDoctorDto.setName("Dr. John Updated");
        newDoctorDto.setSpecializationIds(new HashSet<>(Set.of(specialization.getId())));

        GeneralPractitionerDto updateGpDto = new GeneralPractitionerDto(newDoctorDto, Set.of(patient.getId()));

        when(gpRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gpService.updateDoctor(2L, updateGpDto);
        });

        assertEquals("No GP found with id: 2", exception.getMessage());

    }

    @Test
    void generalPractitionerService_deleteDoctor_returnsVoid() {
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));

        gpService.deleteDoctor(2L);

        verify(gpRepository).save(gp);
    }

    @Test
    void generalPractitionerService_deleteDoctor_throwsEntityNotFound() {
        when(gpRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gpService.deleteDoctor(2L);
        });

        assertEquals("No GP found with id: 2", exception.getMessage());
    }

    @Test
    void generalPractitionerService_addSpecialization_returnsGpDto() {
        Specialization specialization = new Specialization();
        specialization.setId(2L);
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(specializationRepository.findById(2L)).thenReturn(Optional.of(specialization));

        gpService.addSpecialization(2L, 2L);

        verify(gpRepository).save(gp);
        assertTrue(gp.getSpecializations().contains(specialization));
    }

    @Test
    void generalPractitionerService_addSpecialization_throwsEntityNotFound() {
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(specializationRepository.findById(3L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gpService.addSpecialization(2L, 3L);
        });

        assertEquals("No specialization found with id: 3", exception.getMessage());
    }

    @Test
    void generalPractitionerService_removeSpecialization_returnsGpDto() {
        Specialization specializationToRemove = new Specialization();
        specializationToRemove.setId(2L);
        specializationToRemove.setName("Bla bla");
        specializationToRemove.setDeleted(false);
        specializationToRemove.setDoctors(new HashSet<>());

        gp.getSpecializations().add(specializationToRemove);

        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(specializationRepository.findById(2L)).thenReturn(Optional.of(specializationToRemove));

        gpService.removeSpecialization(2L, 2L);

        verify(gpRepository).save(gp);
        assertFalse(gp.getSpecializations().contains(specializationToRemove));
    }

    @Test
    void generalPractitionerService_removeSpecialization_throwsEntityNotFound() {
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(specializationRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gpService.removeSpecialization(2L, 2L);
        });

        assertEquals("No specialization found with id: 2", exception.getMessage());
    }

    @Test
    void generalPractitionerService_addPatient_returnsGpDto() {
        Patient addPatient = new Patient();
        addPatient.setId(2L);
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(addPatient));

        gpService.addPatient(2L, 2L);

        verify(gpRepository).save(gp);
        assertTrue(gp.getPatients().contains(addPatient));
    }

    @Test
    void generalPractitionerService_addPatient_throwsEntityNotFound() {
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gpService.addPatient(2L, 2L);
        });

        assertEquals("No patient found with id: 2", exception.getMessage());
    }

    @Test
    void generalPractitionerService_removePatient_returnsGpDto() {
        Patient removePatient = new Patient();
        removePatient.setId(2L);
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(removePatient));

        gpService.removePatient(2L, 2L);

        verify(gpRepository).save(gp);
        assertFalse(gp.getPatients().contains(removePatient));
    }

    @Test
    void generalPractitionerService_removePatient_throwsEntityNotFound() {
        when(gpRepository.findById(2L)).thenReturn(Optional.of(gp));
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            gpService.removePatient(2L, 2L);
        });

        assertEquals("No patient found with id: 2", exception.getMessage());
    }

    @Test
    void generalPractitionerService_getAllGeneralPractitionersWithPatientCount_returnsGpPatientsCountDto() {
        GpPatientsCountDto gpCountDto = new GpPatientsCountDto(2L, "Dr. Lekar", 1L);
        Set<GpPatientsCountDto> expectedResult = Set.of(gpCountDto);

        when(gpRepository.findAllDoctorsWithPatientCount()).thenReturn(expectedResult);

        Set<GpPatientsCountDto> result = gpService.getAllGeneralPractitionersWithPatientCount();

        assertNotNull(result);
        assertEquals(expectedResult.size(), result.size());
        assertEquals(expectedResult, result);
    }

    @Test
    void generalPractitionerService_getGeneralPractitionerWithPatientCount_returnsGpPatientsCountDto() {
        GpPatientsCountDto gpCountDto = new GpPatientsCountDto(2L, "Dr. Lekar", 1L);

        when(gpRepository.existsById(gp.getId())).thenReturn(true);
        when(gpRepository.findGeneralPractitionerWithPatientCount(2L)).thenReturn(gpCountDto);

        GpPatientsCountDto result = gpService.getGeneralPractitionerWithPatientCount(2L);

        assertNotNull(result);
        assertEquals(gpCountDto, result);
    }

}
