package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.model.Treatment;
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

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TreatmentRepositoryTest {

    @Autowired
    private SpecializationRepository specializationRepository;
    @Autowired
    private DiagnoseRepository diagnoseRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private GeneralPractitionerRepository gpRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorAppointmentRepository appointmentRepository;
    @Autowired
    private TreatmentRepository treatmentRepository;

    private Specialization specialization;
    private Treatment treatment;
    private Diagnose diagnose;
    private Doctor doctor;
    private GeneralPractitioner gp;
    private Patient patient;
    private DoctorAppointment appointment1;
    private DoctorAppointment appointment;

    @BeforeEach
    public void setUp() {
        specialization = new Specialization();
        specialization.setName("Cardiology");
        specialization = specializationRepository.save(specialization);

        diagnose = new Diagnose();
        diagnose.setName("Flu");
        diagnose.setDescription("A contagious respiratory illness caused by influenza viruses, " +
                "leading to fever, cough, sore throat, body aches, and fatigue.");
        diagnose = diagnoseRepository.save(diagnose);

        doctor = new Doctor();
        doctor.setName("Dr. Doctor Doctorov");
        doctor.setSpecializations(new HashSet<>(Set.of(specialization)));
        doctor = doctorRepository.save(doctor);

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

        treatment = new Treatment();
        treatment.setMedicineName("Ibuprofen");
        treatment.setDosageAmount("200mg");
        treatment.setFrequency("Twice a day");
        treatment = treatmentRepository.save(treatment);

        appointment = new DoctorAppointment();
        appointment.setVisitDate(LocalDate.of(2025, 2, 1));
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDiagnoses(new HashSet<>(Set.of(diagnose)));
        appointment = appointmentRepository.save(appointment);
    }

    @AfterEach
    public void tearDown() {
        // Deleting in reverse order to avoid foreign key constraint issues
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
        gpRepository.deleteAll();
        doctorRepository.deleteAll();
        diagnoseRepository.deleteAll();
        specializationRepository.deleteAll();
    }

    @Test
    public void treatmentRepo_findAll_returnsAllTreatments() {
        List<Treatment> treatments = treatmentRepository.findAll();
        assertThat(treatments).isNotNull().hasSize(1);
    }

    @Test
    public void treatmentRepo_findById_returnsTreatment() {
        Optional<Treatment> foundTreatment = treatmentRepository.findById(treatment.getId());
        assertThat(foundTreatment).isPresent();
        assertThat(foundTreatment.get().getMedicineName()).isEqualTo("Ibuprofen");
    }

    @Test
    public void treatmentRepo_save_savesTreatment() {
        Treatment newTreatment = new Treatment();
        newTreatment.setMedicineName("Paracetamol");
        newTreatment.setDosageAmount("500mg");
        newTreatment.setFrequency("Once a day");

        Treatment savedTreatment = treatmentRepository.save(newTreatment);

        assertThat(savedTreatment).isNotNull();
        assertThat(savedTreatment.getId()).isNotNull();
        assertThat(savedTreatment.getMedicineName()).isEqualTo("Paracetamol");
    }

    @Test
    public void treatmentRepo_delete_removesTreatment() {
        treatmentRepository.delete(treatment);
        Optional<Treatment> deletedTreatment = treatmentRepository.findById(treatment.getId());
        assertThat(deletedTreatment).isNotPresent();
    }

}
