package com.nbu.medicalrecordf104458.controller;

import com.nbu.medicalrecordf104458.dto.PatientDto;
import com.nbu.medicalrecordf104458.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@AllArgsConstructor
@RequestMapping("/patients")
@RestController
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/")
    public ResponseEntity<Set<PatientDto>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping("/")
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto dto) {
        PatientDto createdDto = patientService.createPatient(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDto dto) {
        PatientDto updatedDto = patientService.updatePatient(id, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/insurance-status")
    public ResponseEntity<String> checkInsuranceStatus(@PathVariable Long id) {
        boolean hasPaid = patientService.isInsurancePaidLast6Months(id);

        if (hasPaid) {
            return ResponseEntity.ok("The patient has paid their insurance in the last 6 months.");
        } else {
            return ResponseEntity.status(400).body("The patient has not paid their insurance in the last 6 months.");
        }
    }

    @GetMapping("/patients-by-diagnose/{diagnoseId}")
    public ResponseEntity<Set<PatientDto>> getPatientsByDiagnose(@PathVariable Long diagnoseId) {
        Set<PatientDto> patients = patientService.getPatientsByDiagnoseId(diagnoseId);
        return ResponseEntity.ok(patients);
    }

}
