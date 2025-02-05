package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.mapper.DoctorAppointmentMapper;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.service.implementation.DoctorAppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class DoctorAppointmentServiceTest {

    @Mock
    private DoctorAppointmentMapper appointmentMapper;

    @Mock
    private DoctorAppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorAppointmentServiceImpl appointmentService;

    private Doctor doctor;
    private AppointmentDto appointmentDto;
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

        appointmentDto = new AppointmentDto();
        appointmentDto.setId(1L);
        appointmentDto.setVisitDate(LocalDate.of(2025, 1, 31));
        appointmentDto.setDoctorId(doctor.getId());
        appointmentDto.setPatientId(patient.getId());
        appointmentDto.setDiagnoses(new HashSet<>(Set.of(diagnose.getId())));
        appointmentDto.setSickLeaveId(sickLeave.getId());
    }

    @Test
    void doctorAppointmentService_getAppointmentById_returnsDoctorDto() {

    }

    @Test
    void doctorAppointmentService_getAppointmentById_throwsEntityNotFound() {

    }

    @Test
    void doctorAppointmentService_createAppointment_returnsDoctorDto() {

    }

    @Test
    void doctorAppointmentService_createAppointment_throwsIllegalArgumentException() {

    }

    @Test
    void doctorAppointmentService_updateAppointment_returnsDoctorDto() {

    }

    @Test
    void doctorAppointmentService_updateAppointment_throwsEntityNotFound() {

    }

    @Test
    void doctorAppointmentService_deleteAppointment_returnsVoid() {

    }

    @Test
    void doctorAppointmentService_deleteAppointment_throwsEntityNotFound() {

    }

}
