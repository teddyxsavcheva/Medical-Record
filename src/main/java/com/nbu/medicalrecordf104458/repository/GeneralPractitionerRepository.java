package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.dto.queries.GpPatientsCountDto;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GeneralPractitionerRepository extends JpaRepository<GeneralPractitioner, Long> {

    Set<GeneralPractitioner> findAllByDeletedFalse();

    // d. - for all GPs (excluding deleted ones)
    @Query("SELECT new com.nbu.medicalrecordf104458.dto.queries.GpPatientsCountDto(gp.id, gp.name, COUNT(p.id)) " +
            "FROM GeneralPractitioner gp LEFT JOIN gp.patients p " +
            "WHERE gp.deleted = false " +
            "GROUP BY gp.id, gp.name")
    Set<GpPatientsCountDto> findAllDoctorsWithPatientCount();

    // d. - for a specific GP (excluding deleted ones)
    @Query("SELECT new com.nbu.medicalrecordf104458.dto.queries.GpPatientsCountDto(gp.id, gp.name, COUNT(p.id)) " +
            "FROM GeneralPractitioner gp LEFT JOIN gp.patients p " +
            "WHERE gp.id = :generalPractitionerId AND gp.deleted = false " +
            "GROUP BY gp.id, gp.name")
    GpPatientsCountDto findGeneralPractitionerWithPatientCount(@Param("generalPractitionerId") Long generalPractitionerId);

}
