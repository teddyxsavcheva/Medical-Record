package com.nbu.medicalrecordf104458.repository;

import com.nbu.medicalrecordf104458.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}
