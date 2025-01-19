package com.nbu.medicalrecordf104458.controller;

import com.nbu.medicalrecordf104458.dto.GeneralPractitionerDto;
import com.nbu.medicalrecordf104458.service.GeneralPractitionerService;
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
@RequestMapping("/general-practitioners")
@RestController
public class GeneralPractitionerController {

    private final GeneralPractitionerService gpService;

    @GetMapping("/")
    public ResponseEntity<Set<GeneralPractitionerDto>> getAllDoctors() {
        return ResponseEntity.ok(gpService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralPractitionerDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(gpService.getDoctorById(id));
    }

    @PostMapping("/")
    public ResponseEntity<GeneralPractitionerDto> createDoctor(@Valid @RequestBody GeneralPractitionerDto dto) {
        GeneralPractitionerDto createdDto = gpService.createDoctor(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralPractitionerDto> updateDoctor(@PathVariable Long id, @Valid @RequestBody GeneralPractitionerDto dto) {
        GeneralPractitionerDto updatedDto = gpService.updateDoctor(id, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDoctor(@PathVariable Long id) {
        gpService.deleteDoctor(id);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Add/Remove methods for Specialization many-to-many table
    @PostMapping("/{gpId}/specializations/{specializationId}")
    public ResponseEntity<GeneralPractitionerDto> addSpecialization(@PathVariable Long gpId, @PathVariable Long specializationId) {
        GeneralPractitionerDto updatedDto = gpService.addSpecialization(gpId, specializationId);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{gpId}/specializations/{specializationId}")
    public ResponseEntity<GeneralPractitionerDto> removeSpecialization(@PathVariable Long gpId, @PathVariable Long specializationId) {
        GeneralPractitionerDto updatedDto = gpService.removeSpecialization(gpId, specializationId);

        return ResponseEntity.ok(updatedDto);
    }

    // Add/Remove methods for Patient many-to-many table
    @PostMapping("/{gpId}/patients/{patientId}")
    public ResponseEntity<GeneralPractitionerDto> addPatient(@PathVariable Long gpId, @PathVariable Long patientId) {
        GeneralPractitionerDto updatedDto = gpService.addPatient(gpId, patientId);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{gpId}/patients/{patientId}")
    public ResponseEntity<GeneralPractitionerDto> removePatient(@PathVariable Long gpId, @PathVariable Long patientId) {
        GeneralPractitionerDto updatedDto = gpService.removePatient(gpId, patientId);

        return ResponseEntity.ok(updatedDto);
    }

}
