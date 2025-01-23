package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralPractitionerRepository extends JpaRepository<GeneralPractitioner, Long> {

    @Query("SELECT COUNT(p) FROM GeneralPractitioner gp JOIN gp.patients p WHERE gp.id = :gpId")
    Long countPatientsByGPId(@Param("gpId") Long gpId);

}
