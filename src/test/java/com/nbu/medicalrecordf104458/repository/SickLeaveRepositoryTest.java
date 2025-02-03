package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SickLeaveRepositoryTest {

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
    private SickLeaveRepository sickLeaveRepository;

    private Specialization specialization;
    private Diagnose diagnose;
    private Doctor doctor;
    private GeneralPractitioner gp;
    private Patient patient;
    private DoctorAppointment appointment;
    private SickLeave sickLeave;

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

        appointment = new DoctorAppointment();
        appointment.setVisitDate(LocalDate.of(2025, 1, 31));
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDiagnoses(Set.of(diagnose));
        appointment = appointmentRepository.save(appointment);

        sickLeave = new SickLeave();
        sickLeave.setStartDate(LocalDate.of(2025, 2, 1));
        sickLeave.setEndDate(LocalDate.of(2025, 2, 10));
        sickLeave.setDoctorAppointment(appointment);
        sickLeave = sickLeaveRepository.save(sickLeave);

        // Свързване на SickLeave с DoctorAppointment
        appointment.setSickLeave(sickLeave);
        appointmentRepository.save(appointment);
    }

    @AfterEach
    public void tearDown() {
        sickLeaveRepository.deleteAll();
        appointmentRepository.deleteAll();
    }

    @Test
    public void sickLeaveRepo_findById_returnsSickLeave() {
        Optional<SickLeave> foundSickLeave = sickLeaveRepository.findById(sickLeave.getId());
        assertThat(foundSickLeave).isPresent();
        assertThat(foundSickLeave.get().getStartDate()).isEqualTo(LocalDate.of(2025, 2, 1));
    }

    @Test
    public void sickLeaveRepo_save_savesSickLeave() {
        SickLeave newSickLeave = new SickLeave();
        newSickLeave.setStartDate(LocalDate.of(2025, 3, 1));
        newSickLeave.setEndDate(LocalDate.of(2025, 3, 10));
        newSickLeave.setDoctorAppointment(appointment);
        SickLeave savedSickLeave = sickLeaveRepository.save(newSickLeave);

        assertThat(savedSickLeave).isNotNull();
        assertThat(savedSickLeave.getId()).isNotNull();
        assertThat(savedSickLeave.getStartDate()).isEqualTo(LocalDate.of(2025, 3, 1));
    }

    @Test
    public void sickLeaveRepo_delete_removesSickLeave() {
        sickLeaveRepository.delete(sickLeave);
        Optional<SickLeave> deletedSickLeave = sickLeaveRepository.findById(sickLeave.getId());

        assertThat(deletedSickLeave).isNotPresent();
    }

}
