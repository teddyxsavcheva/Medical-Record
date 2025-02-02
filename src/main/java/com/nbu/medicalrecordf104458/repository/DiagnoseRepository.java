package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.Diagnose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DiagnoseRepository extends JpaRepository<Diagnose, Long> {

    Set<Diagnose> findAllByDeletedFalse();

}
