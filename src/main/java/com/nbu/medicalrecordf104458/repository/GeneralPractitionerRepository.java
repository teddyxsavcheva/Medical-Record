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

    // d. - for certain GP
    @Query("SELECT new com.nbu.medicalrecordf104458.dto.queries.GpPatientsCountDto(gp.id, gp.name, COUNT(p.id)) " +
            "FROM GeneralPractitioner gp LEFT JOIN gp.patients p " +
            "GROUP BY gp.id, gp.name")
    Set<GpPatientsCountDto> findAllDoctorsWithPatientCount();

    // d. - for all GPs
    @Query("SELECT new com.nbu.medicalrecordf104458.dto.queries.GpPatientsCountDto(gp.id, gp.name, COUNT(p.id)) " +
            "FROM GeneralPractitioner gp LEFT JOIN gp.patients p " +
            "WHERE gp.id = :generalPractitionerId " +
            "GROUP BY gp.id, gp.name")
    GpPatientsCountDto findGeneralPractitionerWithPatientCount(@Param("generalPractitionerId") Long generalPractitionerId);


}
