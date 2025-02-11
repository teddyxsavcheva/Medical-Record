package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.PatientDto;
import com.nbu.medicalrecordf104458.mapper.DoctorAppointmentMapper;
import com.nbu.medicalrecordf104458.mapper.PatientMapper;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.repository.DiagnoseRepository;
import com.nbu.medicalrecordf104458.repository.GeneralPractitionerRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PatientServiceImpl implements PatientService {

    private final PatientMapper mapper;
    private final DoctorAppointmentMapper appointmentMapper;
    private final PatientRepository patientRepository;
    private final GeneralPractitionerRepository gpRepository;
    private final DiagnoseRepository diagnoseRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public Set<PatientDto> getAllPatients() {

        return patientRepository.findAllByDeletedFalse().stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR') or @customSecurityChecker.isPatientAccessingOwnData(#id)")
    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .filter(patient1 -> !patient1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + id));

        return mapper.convertToDto(patient);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public PatientDto createPatient(PatientDto patientDto) {
        Patient patient = mapper.convertToEntity(patientDto);

        if (patient.getFamilyDoctor().isDeleted()) {
            throw new IllegalArgumentException("You can't use records that are marked for deletion!");
        }

        return mapper.convertToDto(patientRepository.save(patient));
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public PatientDto updatePatient(Long id, PatientDto patientDto) {
        Patient patient = patientRepository.findById(id)
                .filter(patient1 -> !patient1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + id));

        patient.setName(patientDto.getName());
        patient.setUnifiedCivilNumber(patientDto.getUnifiedCivilNumber());
        patient.setLastInsurancePayment(patientDto.getLastInsurancePayment());

        GeneralPractitioner gp = gpRepository.findById(patientDto.getFamilyDoctorId())
                .filter(generalPractitioner -> !generalPractitioner.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + patientDto.getFamilyDoctorId()));

        patient.setFamilyDoctor(gp);

        return mapper.convertToDto(patientRepository.save(patient));
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .filter(patient1 -> !patient1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + id));

        patient.setDeleted(true);

        patientRepository.save(patient);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR') or @customSecurityChecker.isPatientAccessingOwnData(#patientId)")
    public boolean isInsurancePaidLast6Months(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .filter(patient1 -> !patient1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + patientId));

        LocalDate currentDate = LocalDate.now();
        LocalDate lastPaymentDate = patient.getLastInsurancePayment();

        long monthsBetween = ChronoUnit.MONTHS.between(lastPaymentDate, currentDate);

        return monthsBetween <= 6;
    }

    // Queries

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public Set<PatientDto> getPatientsByDiagnoseId(Long diagnoseId) {
        diagnoseRepository.findById(diagnoseId)
                .filter(diagnose -> !diagnose.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No Diagnose found with id: " + diagnoseId));

        return patientRepository.findPatientsByDiagnoseId(diagnoseId).stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public Set<PatientDto> getPatientsByGeneralPractitioner(Long gpId) {
        gpRepository.findById(gpId)
                .filter(generalPractitioner -> !generalPractitioner.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + gpId));

        return patientRepository.findPatientsByGeneralPractitionerId(gpId).stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR') or @customSecurityChecker.isPatientAccessingOwnData(#patientId)")
    public Set<AppointmentDto> getVisitsByPatient(Long patientId) {
        patientRepository.findById(patientId)
                .filter(patient -> !patient.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No Patient found with id: " + patientId));

        return patientRepository.findVisitsByPatientId(patientId).stream()
                .map(appointmentMapper::convertToDto)
                .collect(Collectors.toSet());
    }

}
