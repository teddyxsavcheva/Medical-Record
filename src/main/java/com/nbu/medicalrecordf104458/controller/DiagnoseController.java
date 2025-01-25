package com.nbu.medicalrecordf104458.controller;

import com.nbu.medicalrecordf104458.dto.DiagnoseDto;
import com.nbu.medicalrecordf104458.service.DiagnoseService;
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
@RequestMapping("/diagnoses")
@RestController
public class DiagnoseController {

    private final DiagnoseService diagnoseService;

    @GetMapping("/")
    public ResponseEntity<Set<DiagnoseDto>> getAllDiagnoses() {
        return ResponseEntity.ok(diagnoseService.getAllDiagnoses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnoseDto> getDiagnoseById(@PathVariable Long id) {
        return ResponseEntity.ok(diagnoseService.getDiagnoseById(id));
    }

    @PostMapping("/")
    public ResponseEntity<DiagnoseDto> createDiagnose(@Valid @RequestBody DiagnoseDto diagnoseDto) {
        DiagnoseDto createdDto = diagnoseService.createDiagnose(diagnoseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiagnoseDto> updateDiagnose(@PathVariable Long id, @Valid @RequestBody DiagnoseDto diagnoseDto) {
        DiagnoseDto updatedDto = diagnoseService.updateDiagnose(id, diagnoseDto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDiagnose(@PathVariable Long id) {
        diagnoseService.deleteDiagnose(id);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Add/Remove Appointments in many-to-many table

    @PostMapping("/{diagnoseId}/appointments/{appointmentId}")
    public ResponseEntity<DiagnoseDto> addAppointment(@PathVariable Long diagnoseId, @PathVariable Long appointmentId) {
        DiagnoseDto updatedDto = diagnoseService.addAppointment(diagnoseId, appointmentId);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{diagnoseId}/appointments/{appointmentId}")
    public ResponseEntity<DiagnoseDto> removeAppointment(@PathVariable Long diagnoseId, @PathVariable Long appointmentId) {
        DiagnoseDto updatedDto = diagnoseService.removeAppointment(diagnoseId, appointmentId);

        return ResponseEntity.ok(updatedDto);
    }

    // Queries

    @GetMapping("/most-common-diagnoses")
    public ResponseEntity<Set<DiagnoseDto>> getMostCommonDiagnoses() {
        return ResponseEntity.ok(diagnoseService.findMostCommonDiagnoses());
    }

}
