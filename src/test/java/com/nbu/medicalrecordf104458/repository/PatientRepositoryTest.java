package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
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
public class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private GeneralPractitionerRepository gpRepository;
    @Autowired
    private DiagnoseRepository diagnoseRepository;
    @Autowired
    private SpecializationRepository specializationRepository;
    @Autowired
    private DoctorAppointmentRepository appointmentRepository;

    private Specialization specialization;
    private GeneralPractitioner gp;
    private Diagnose diagnose;
    private Patient patient;
    private DoctorAppointment appointment1;

    @BeforeEach
    public void setUp() {
        specialization = new Specialization();
        specialization.setName("Cardiology");
        specialization = specializationRepository.save(specialization);

        gp = new GeneralPractitioner();
        gp.setName("Dr. Lichen Lekar");
        gp.setSpecializations(new HashSet<>(Set.of(specialization)));
        gp = gpRepository.save(gp);

        diagnose = new Diagnose();
        diagnose.setName("Flu");
        diagnose.setDescription("A contagious respiratory illness caused by influenza viruses, leading to fever, cough, sore throat, body aches, and fatigue.");
        diagnose = diagnoseRepository.save(diagnose);

        patient = new Patient();
        patient.setName("Pacient Pacientov");
        patient.setFamilyDoctor(gp);
        patient.setLastInsurancePayment(LocalDate.of(2025, 1, 31));
        patient.setUnifiedCivilNumber(1234L);
        patient = patientRepository.save(patient);

        appointment1 = new DoctorAppointment();
        appointment1.setVisitDate(LocalDate.of(2025, 1, 31));
        appointment1.setDoctor(gp);
        appointment1.setPatient(patient);
        appointment1.setDiagnoses(Set.of(diagnose));
        appointment1 = appointmentRepository.save(appointment1);
    }

    @AfterEach
    public void tearDown() {
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
        gpRepository.deleteAll();
        diagnoseRepository.deleteAll();
        specializationRepository.deleteAll();
    }

    @Test
    public void patientRepo_findById_returnsPatient() {
        Optional<Patient> foundPatient = patientRepository.findById(patient.getId());
        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get().getName()).isEqualTo("Pacient Pacientov");
    }

    @Test
    public void patientRepo_findAll_returnsAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        assertThat(patients).isNotNull().hasSize(1);
    }

    @Test
    public void patientRepo_findById_throwsEntityNotFoundException() {
        patient.setDeleted(true);
        patientRepository.save(patient);
        assertThrows(EntityNotFoundException.class, () ->
                patientRepository.findById(patient.getId())
                        .filter(p -> !p.isDeleted())
                        .orElseThrow(EntityNotFoundException::new)
        );
    }

    @Test
    public void patientRepo_save_savesPatient() {
        Patient newPatient = new Patient();
        newPatient.setName("New Patient");
        newPatient.setFamilyDoctor(gp);
        newPatient.setLastInsurancePayment(LocalDate.of(2025, 2, 1));
        newPatient.setUnifiedCivilNumber(5678L);
        Patient savedPatient = patientRepository.save(newPatient);

        assertThat(savedPatient).isNotNull();
        assertThat(savedPatient.getId()).isNotNull();
        assertThat(savedPatient.getName()).isEqualTo("New Patient");
        assertThat(savedPatient.getFamilyDoctor()).isEqualTo(gp);
    }

    @Test
    public void patientRepo_delete_removesPatient() {
        patient.setDeleted(true);
        patientRepository.save(patient);
        Optional<Patient> deletedPatient = patientRepository.findById(patient.getId());

        assertThat(deletedPatient.get().isDeleted()).isTrue();
    }

    @Test
    public void patientRepo_findPatientsByDiagnoseId_returnsPatients() {
        Set<Patient> patients = patientRepository.findPatientsByDiagnoseId(diagnose.getId());
        assertThat(patients).isNotEmpty();
        assertThat(patients).contains(patient);
    }

    @Test
    public void patientRepo_findPatientsByGeneralPractitionerId_returnsPatients() {
        Set<Patient> patients = patientRepository.findPatientsByGeneralPractitionerId(gp.getId());
        assertThat(patients).isNotEmpty();
        assertThat(patients).contains(patient);
    }

}
