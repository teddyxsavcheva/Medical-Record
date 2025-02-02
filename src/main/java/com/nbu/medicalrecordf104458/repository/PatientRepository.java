package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Set<Patient> findAllByDeletedFalse();

    // a. - Find patients by diagnose (only non-deleted patients)
    @Query("SELECT appointment.patient " +
            "FROM DoctorAppointment appointment " +
            "JOIN appointment.diagnoses diagnose " +
            "WHERE diagnose.id = :diagnoseId AND appointment.patient.deleted = false")
    Set<Patient> findPatientsByDiagnoseId(@Param("diagnoseId") Long diagnoseId);

    // c. - Find patients by GP (only non-deleted patients)
    @Query("SELECT patient " +
            "FROM Patient patient " +
            "WHERE patient.familyDoctor.id = :gpId AND patient.deleted = false")
    Set<Patient> findPatientsByGeneralPractitionerId(@Param("gpId") Long gpId);

    // f. - Find visits by patient (excluding visits for deleted patients)
    @Query("SELECT a " +
            "FROM DoctorAppointment a " +
            "WHERE a.patient.id = :patientId AND a.patient.deleted = false")
    Set<DoctorAppointment> findVisitsByPatientId(@Param("patientId") Long patientId);
}
