package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.Specialization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SpecializationRepositoryTest {

    @Autowired
    private SpecializationRepository specializationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private Specialization specialization;
    private Doctor doctor;

    @BeforeEach
    public void setUp() {
        specialization = new Specialization();
        specialization.setName("Cardiology");
        specialization = specializationRepository.save(specialization);

        doctor = new Doctor();
        doctor.setName("Dr. Doctor Doctorov");
        doctor.setSpecializations(Set.of(specialization));
        doctor = doctorRepository.save(doctor);
    }

    @AfterEach
    public void tearDown() {
        doctorRepository.deleteAll();
        specializationRepository.deleteAll();
    }

    @Test
    public void specializationRepo_findAll_returnsAllSpecializations() {
        List<Specialization> specializations = specializationRepository.findAll();
        assertThat(specializations).isNotNull().hasSize(1);
    }

    @Test
    public void specializationRepo_findById_returnsSpecialization() {
        Optional<Specialization> foundSpecialization = specializationRepository.findById(specialization.getId());
        assertThat(foundSpecialization).isPresent();
        assertThat(foundSpecialization.get().getName()).isEqualTo("Cardiology");
    }

    @Test
    public void specializationRepo_findAllByDeletedFalse_returnsActiveSpecializations() {
        Set<Specialization> specializations = specializationRepository.findAllByDeletedFalse();
        assertThat(specializations).isNotNull().hasSize(1);
    }

    @Test
    public void specializationRepo_save_savesSpecialization() {
        Specialization newSpecialization = new Specialization();
        newSpecialization.setName("Neurology");
        Specialization savedSpecialization = specializationRepository.save(newSpecialization);

        assertThat(savedSpecialization).isNotNull();
        assertThat(savedSpecialization.getId()).isNotNull();
        assertThat(savedSpecialization.getName()).isEqualTo("Neurology");
    }

    @Test
    public void specializationRepo_delete_removesSpecialization() {
        specialization.setDeleted(true);
        specializationRepository.save(specialization);
        Optional<Specialization> deletedSpecialization = specializationRepository.findById(specialization.getId());
        assertThat(deletedSpecialization.get().isDeleted()).isTrue();
    }
}
