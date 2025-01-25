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

    // a.
    @Query("SELECT appointment.patient FROM DoctorAppointment appointment JOIN appointment.diagnoses diagnose WHERE diagnose.id = :diagnoseId")
    Set<Patient> findPatientsByDiagnoseId(@Param("diagnoseId") Long diagnoseId);

    // c.
    @Query("SELECT patient FROM Patient patient WHERE patient.familyDoctor.id = :gpId")
    Set<Patient> findPatientsByGeneralPractitionerId(@Param("gpId") Long gpId);

    // f.
    @Query("SELECT a FROM DoctorAppointment a WHERE a.patient.id = :patientId")
    Set<DoctorAppointment> findVisitsByPatientId(Long patientId);

}
