package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.Specialization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DoctorAppointmentRepositoryTest {

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

    private Specialization specialization;
    private Diagnose diagnose;
    private Doctor doctor;
    private GeneralPractitioner gp;
    private Patient patient;
    private DoctorAppointment appointment1;
    private DoctorAppointment appointment2;

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
        doctor.setSpecializations(Set.of(specialization));
        doctor = doctorRepository.save(doctor);

        gp = new GeneralPractitioner();
        gp.setName("Dr. Lichen Lekar");
        gp.setSpecializations(Set.of(specialization));
        gp = gpRepository.save(gp);

        patient = new Patient();
        patient.setName("Pacient Pacientov");
        patient.setFamilyDoctor(gp);
        patient.setLastInsurancePayment(LocalDate.of(2025, 1, 31));
        patient.setUnifiedCivilNumber(1234L);
        patient = patientRepository.save(patient);

        appointment1 = new DoctorAppointment();
        appointment1.setVisitDate(LocalDate.of(2025, 1, 31));
        appointment1.setDoctor(doctor);
        appointment1.setPatient(patient);
        appointment1.setDiagnoses(Set.of(diagnose));
        appointment1 = appointmentRepository.save(appointment1);

        appointment2 = new DoctorAppointment();
        appointment2.setVisitDate(LocalDate.of(2025, 2, 1));
        appointment2.setDoctor(doctor);
        appointment2.setPatient(patient);
        appointment2.setDiagnoses(Set.of(diagnose));
        appointment2 = appointmentRepository.save(appointment2);
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
    public void appointmentRepo_findAll_returnsAllAppointments() {
        List<DoctorAppointment> appointments = appointmentRepository.findAll();
        assertThat(appointments).isNotNull().hasSize(2);
    }

    @Test
    public void appointmentRepo_findById_returnsAppointment() {
        Optional<DoctorAppointment> foundAppointment = appointmentRepository.findById(appointment1.getId());
        assertThat(foundAppointment).isPresent();
        assertThat(foundAppointment.get().getVisitDate()).isEqualTo(LocalDate.of(2025, 1, 31));
    }

    @Test
    public void appointmentRepo_findVisitsByDateRange_returnsCorrectAppointments() {
        Set<DoctorAppointment> results = appointmentRepository.findVisitsByDateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 11));
        assertThat(results).hasSize(2).contains(appointment1, appointment2);
    }

    @Test
    public void appointmentRepo_findAppointmentsByDoctorAndDateRange_returnsCorrectAppointments() {
        Set<DoctorAppointment> results = appointmentRepository.findAppointmentsByDoctorAndDateRange(doctor.getId(), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 11));
        assertThat(results).hasSize(2).contains(appointment1, appointment2);
    }

    @Test
    public void appointmentRepo_save_savesAppointment() {
        DoctorAppointment newAppointment = new DoctorAppointment();
        newAppointment.setVisitDate(LocalDate.of(2025, 2, 6));
        newAppointment.setDoctor(doctor);
        newAppointment.setPatient(patient);
        newAppointment.setDiagnoses(Set.of(diagnose));

        DoctorAppointment savedAppointment = appointmentRepository.save(newAppointment);

        assertThat(savedAppointment).isNotNull();
        assertThat(savedAppointment.getId()).isNotNull();
        assertThat(savedAppointment.getVisitDate()).isEqualTo(LocalDate.of(2025, 2, 6));
        assertThat(savedAppointment.getDoctor()).isEqualTo(doctor);
        assertThat(savedAppointment.getPatient()).isEqualTo(patient);
        assertThat(savedAppointment.getDiagnoses()).isEqualTo(Set.of(diagnose));
    }

    @Test
    public void appointmentRepo_delete_removesAppointment() {
        appointmentRepository.delete(appointment1);
        Optional<DoctorAppointment> deletedAppointment = appointmentRepository.findById(appointment1.getId());
        assertThat(deletedAppointment).isNotPresent();
    }

}
