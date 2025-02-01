package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.Diagnose;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
// In-memory DB to simulate the real one as it's not a good idea to add/remove things there
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DiagnoseRepositoryTest {

    // Because we are testing, we don't have to worry much about the constructor
    @Autowired
    private DiagnoseRepository diagnoseRepository;

    private Diagnose fluDiagnose;
    private Diagnose covidDiagnose;
    private Diagnose allergyDiagnose;

    @BeforeEach
    public void setUp() {
        fluDiagnose = new Diagnose();
        fluDiagnose.setName("Flu");
        fluDiagnose.setDescription("A contagious respiratory illness caused by influenza viruses, " +
                "leading to fever, cough, sore throat, body aches, and fatigue.");

        covidDiagnose = new Diagnose();
        covidDiagnose.setName("COVID-19");
        covidDiagnose.setDescription("A respiratory illness caused by the SARS-CoV-2 virus, " +
                "characterized by symptoms such as fever, dry cough, difficulty breathing, fatigue, and loss of taste or smell.");

        allergyDiagnose = new Diagnose();
        allergyDiagnose.setName("Allergy");
        allergyDiagnose.setDescription("An immune system reaction to substances (allergens) like pollen, dust, " +
                "or certain foods, which can cause symptoms ranging from sneezing and itching to more severe reactions such as anaphylaxis.");

        diagnoseRepository.save(fluDiagnose);
        diagnoseRepository.save(covidDiagnose);
        diagnoseRepository.save(allergyDiagnose);
    }

    @AfterEach
    public void tearDown() {
        diagnoseRepository.deleteAll();
    }

    @Test
    public void diagnoseRepo_findAll_returnsAllDiagnoses() {
        List<Diagnose> diagnoses = diagnoseRepository.findAll();

        assertThat(diagnoses).isNotNull();
        assertThat(diagnoses.size()).isEqualTo(3);
        assertThat(diagnoses).extracting(Diagnose::getName)
                .containsExactlyInAnyOrder("Flu", "COVID-19", "Allergy");
    }

    @Test
    public void diagnoseRepo_findById_returnsDiagnose() {
        Optional<Diagnose> foundDiagnose = diagnoseRepository.findById(fluDiagnose.getId());

        assertThat(foundDiagnose).isPresent();
        assertThat(foundDiagnose.get().getName()).isEqualTo("Flu");
    }

    @Test
    public void diagnoseRepo_findById_throwsEntityNotFoundException() {
        Long notFoundId = 10000L;
        assertThrows(EntityNotFoundException.class, () ->
                diagnoseRepository.findById(notFoundId)
                        .orElseThrow(EntityNotFoundException::new)
        );
    }

    @Test
    public void diagnoseRepo_save_returnsSavedDiagnose() {
        Diagnose newDiagnose = new Diagnose();
        newDiagnose.setName("Asthma");
        newDiagnose.setDescription("Chronic respiratory condition");

        Diagnose savedDiagnose = diagnoseRepository.save(newDiagnose);

        assertThat(savedDiagnose).isNotNull();
        assertThat(savedDiagnose.getId()).isNotNull();
        assertThat(savedDiagnose.getName()).isEqualTo("Asthma");
    }

    @Test
    public void diagnoseRepo_update_returnsUpdatedDiagnose() {
        fluDiagnose.setName("Severe Flu");
        Diagnose updatedDiagnose = diagnoseRepository.save(fluDiagnose);

        assertThat(updatedDiagnose).isNotNull();
        assertThat(updatedDiagnose.getId()).isNotNull();
        assertThat(updatedDiagnose.getName()).isEqualTo("Severe Flu");
    }

    @Test
    public void diagnoseRepo_delete_removesDiagnose() {
        diagnoseRepository.delete(fluDiagnose);
        Optional<Diagnose> deletedDiagnose = diagnoseRepository.findById(fluDiagnose.getId());

        assertThat(deletedDiagnose).isNotPresent();
    }

}
