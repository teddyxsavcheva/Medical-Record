package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

    Set<Specialization> findAllByDeletedFalse();

}
