package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.dto.queries.DoctorAppointmentsCountDto;
import com.nbu.medicalrecordf104458.model.*;
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
public class DoctorRepositoryTest {

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
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
        gpRepository.deleteAll();
        doctorRepository.deleteAll();
        diagnoseRepository.deleteAll();
        specializationRepository.deleteAll();
    }

    @Test
    public void doctorRepo_findById_returnsDoctor() {
        Optional<Doctor> foundDoctor = doctorRepository.findById(doctor.getId());
        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getName()).isEqualTo("Dr. Doctor Doctorov");
    }

    @Test
    public void doctorRepo_findAll_returnsAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        assertThat(doctors).isNotNull().hasSize(2);
    }

    @Test
    public void doctorRepo_save_savesDoctor() {
        Doctor newDoctor = new Doctor();
        newDoctor.setName("Dr. New");
        newDoctor.setSpecializations(Set.of(specialization));
        Doctor savedDoctor = doctorRepository.save(newDoctor);

        assertThat(savedDoctor).isNotNull();
        assertThat(savedDoctor.getId()).isNotNull();
        assertThat(savedDoctor.getName()).isEqualTo("Dr. New");
        assertThat(savedDoctor.getSpecializations()).contains(specialization);
    }

    @Test
    public void doctorRepo_delete_removesDoctor() {
        appointmentRepository.deleteAll();
        doctorRepository.delete(doctor);
        Optional<Doctor> deletedDoctor = doctorRepository.findById(doctor.getId());
        assertThat(deletedDoctor).isNotPresent();
    }

    @Test
    public void doctorRepo_findDoctorWithAppointmentCount_returnsCorrectData() {
        DoctorAppointmentsCountDto result = doctorRepository.findDoctorWithAppointmentCount(doctor.getId());

        assertThat(result).isNotNull();
        assertThat(result.getDoctorId()).isEqualTo(doctor.getId());
        assertThat(result.getDoctorName()).isEqualTo(doctor.getName());
        assertThat(result.getAppointmentsCount()).isEqualTo(2);
    }

    @Test
    public void doctorRepo_findAllDoctorsWithAppointmentCount_returnsCorrectData() {
        Set<DoctorAppointmentsCountDto> results = doctorRepository.findAllDoctorsWithAppointmentCount();

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(dto ->
                dto.getDoctorId().equals(doctor.getId()) &&
                        dto.getDoctorName().equals(doctor.getName()) &&
                        dto.getAppointmentsCount() == 2
        );
    }

}
