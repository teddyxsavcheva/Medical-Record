package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.PatientDto;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.repository.GeneralPractitionerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PatientMapper {

    private final GeneralPractitionerRepository gpRepository;

    public PatientDto convertToDto(Patient patient) {
        PatientDto patientDto = new PatientDto();

        patientDto.setId(patient.getId());
        patientDto.setName(patient.getName());
        patientDto.setUnifiedCivilNumber(patient.getUnifiedCivilNumber());
        patientDto.setLastInsurancePayment(patient.getLastInsurancePayment());
        patientDto.setFamilyDoctorId(patient.getFamilyDoctor().getId());

        return patientDto;
    }

    public Patient convertToEntity(PatientDto patientDto) {
        Patient patient = new Patient();

        patient.setName(patientDto.getName());
        patient.setUnifiedCivilNumber(patientDto.getUnifiedCivilNumber());
        patient.setLastInsurancePayment(patientDto.getLastInsurancePayment());

        GeneralPractitioner gp = gpRepository.findById(patientDto.getFamilyDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + patientDto.getFamilyDoctorId()));

        patient.setFamilyDoctor(gp);

        return patient;
    }

}
