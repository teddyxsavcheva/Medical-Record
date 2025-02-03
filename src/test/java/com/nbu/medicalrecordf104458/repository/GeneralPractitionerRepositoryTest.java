package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.dto.queries.GpPatientsCountDto;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.Specialization;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class GeneralPractitionerRepositoryTest {

    @Autowired
    private GeneralPractitionerRepository gpRepository;
    @Autowired
    private SpecializationRepository specializationRepository;
    @Autowired
    private PatientRepository patientRepository;

    private GeneralPractitioner gp;
    private Specialization specialization;
    private Patient patient;

    @BeforeEach
    public void setUp() {
        specialization = new Specialization();
        specialization.setName("Cardiology");
        specialization = specializationRepository.save(specialization);

        gp = new GeneralPractitioner();
        gp.setName("Dr. Lichen Lekar");
        gp.setSpecializations(new HashSet<>(Set.of(specialization)));
        gp = gpRepository.save(gp);

        patient = new Patient();
        patient.setName("Pacient Pacientov");
        patient.setFamilyDoctor(gp);
        patient.setLastInsurancePayment(LocalDate.of(2025, 1, 31));
        patient.setUnifiedCivilNumber(1234L);
        patient = patientRepository.save(patient);

        gp.setPatients(new HashSet<>(Set.of(patient)));
        gp = gpRepository.save(gp);
    }

    @AfterEach
    public void tearDown() {
        patientRepository.deleteAll();
        gpRepository.deleteAll();
        specializationRepository.deleteAll();
    }

    @Test
    public void gpRepo_findById_returnsGeneralPractitioner() {
        Optional<GeneralPractitioner> foundGp = gpRepository.findById(gp.getId());
        assertThat(foundGp).isPresent();
        assertThat(foundGp.get().getName()).isEqualTo("Dr. Lichen Lekar");
    }

    @Test
    public void gpRepo_findAll_returnsAllGeneralPractitioners() {
        List<GeneralPractitioner> gps = gpRepository.findAll();
        assertThat(gps).isNotNull().hasSize(1);
    }

    @Test
    public void gpRepo_findAllByDeletedFalse_returnsAllActiveGeneralPractitioners() {
        GeneralPractitioner deletedGp = new GeneralPractitioner();
        deletedGp.setName("Deleted GP");
        deletedGp.setSpecializations(new HashSet<>(Set.of(specialization)));
        deletedGp.setDeleted(true);
        gpRepository.save(deletedGp);

        Set<GeneralPractitioner> activeGps = gpRepository.findAllByDeletedFalse();

        assertThat(activeGps).isNotNull();
        assertThat(activeGps).hasSize(1);
        assertThat(activeGps.iterator().next().getName()).isEqualTo("Dr. Lichen Lekar");
    }

    @Test
    public void gpRepo_save_savesGeneralPractitioner() {
        GeneralPractitioner newGp = new GeneralPractitioner();
        newGp.setName("Dr. New GP");
        newGp.setSpecializations(new HashSet<>(Set.of(specialization)));
        GeneralPractitioner savedGp = gpRepository.save(newGp);

        assertThat(savedGp).isNotNull();
        assertThat(savedGp.getId()).isNotNull();
        assertThat(savedGp.getName()).isEqualTo("Dr. New GP");
        assertThat(savedGp.getSpecializations()).contains(specialization);
    }

    @Test
    public void gpRepo_delete_setsDeletedFlag() {
        gp.setDeleted(true);
        gpRepository.save(gp);
        Optional<GeneralPractitioner> deletedGp = gpRepository.findById(gp.getId());

        assertThat(deletedGp).isPresent();
        assertThat(deletedGp.get().isDeleted()).isTrue();
    }

    @Test
    public void gpRepo_findById_throwsEntityNotFoundException() {
        gp.setDeleted(true);
        gpRepository.save(gp);
        assertThrows(EntityNotFoundException.class, () ->
                gpRepository.findById(gp.getId())
                        .filter(generalPractitioner -> !generalPractitioner.isDeleted())
                        .orElseThrow(EntityNotFoundException::new)
        );
    }

    @Test
    public void gpRepo_findAllDoctorsWithPatientCount_returnsCorrectData() {
        Set<GpPatientsCountDto> results = gpRepository.findAllDoctorsWithPatientCount();

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(dto ->
                dto.getDoctorId().equals(gp.getId()) &&
                        dto.getDoctorName().equals(gp.getName()) &&
                        dto.getPatientCount() == 1
        );
    }

    @Test
    public void gpRepo_findGeneralPractitionerWithPatientCount_returnsCorrectData() {
        GpPatientsCountDto result = gpRepository.findGeneralPractitionerWithPatientCount(gp.getId());

        assertThat(result).isNotNull();
        assertThat(result.getDoctorId()).isEqualTo(gp.getId());
        assertThat(result.getDoctorName()).isEqualTo(gp.getName());
        assertThat(result.getPatientCount()).isEqualTo(1);
    }

}
