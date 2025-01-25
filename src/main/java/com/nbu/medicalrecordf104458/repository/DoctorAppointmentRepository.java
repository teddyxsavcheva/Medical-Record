package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface DoctorAppointmentRepository extends JpaRepository<DoctorAppointment, Long> {

    // g.
    @Query("SELECT a FROM DoctorAppointment a WHERE a.visitDate BETWEEN :startDate AND :endDate")
    Set<DoctorAppointment> findVisitsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // h.
    @Query("SELECT a FROM DoctorAppointment a WHERE a.doctor.id = :doctorId AND a.visitDate BETWEEN :startDate AND :endDate")
    Set<DoctorAppointment> findAppointmentsByDoctorAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
