package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.dto.queries.DoctorAppointmentsCountDto;
import com.nbu.medicalrecordf104458.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Set<Doctor> findAllByDeletedFalse();

    // e. - for certain doctor (excluding deleted ones)
    @Query("SELECT new com.nbu.medicalrecordf104458.dto.queries.DoctorAppointmentsCountDto(d.id, d.name, COUNT(a)) " +
            "FROM Doctor d LEFT JOIN d.appointments a " +
            "WHERE d.id = :doctorId AND d.deleted = false " +
            "GROUP BY d.id, d.name")
    DoctorAppointmentsCountDto findDoctorWithAppointmentCount(@Param("doctorId") Long doctorId);

    // e. - for all doctors (excluding deleted ones)
    @Query("SELECT new com.nbu.medicalrecordf104458.dto.queries.DoctorAppointmentsCountDto(d.id, d.name, COUNT(a)) " +
            "FROM Doctor d LEFT JOIN d.appointments a " +
            "WHERE d.deleted = false " +
            "GROUP BY d.id, d.name")
    Set<DoctorAppointmentsCountDto> findAllDoctorsWithAppointmentCount();

}
