package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DoctorMapper {

    private final SpecializationRepository specializationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public DoctorDto convertToDto(Doctor doctor) {
        DoctorDto dto = new DoctorDto();


        return dto;
    }

}
