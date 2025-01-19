package com.nbu.medicalrecordf104458.controller;

import com.nbu.medicalrecordf104458.dto.TreatmentDto;
import com.nbu.medicalrecordf104458.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@AllArgsConstructor
@RequestMapping("/treatments")
@RestController
public class TreatmentController {

    private final TreatmentService treatmentService;

    @GetMapping("/")
    public ResponseEntity<Set<TreatmentDto>> getAllTreatments() {
        return ResponseEntity.ok(treatmentService.getAllTreatments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreatmentDto> getTreatmentById(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentService.getTreatmentById(id));
    }

    @PostMapping("/")
    public ResponseEntity<TreatmentDto> createTreatment(@Valid @RequestBody TreatmentDto dto) {
        TreatmentDto createdDto = treatmentService.createTreatment(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreatmentDto> updateTreatment(@PathVariable Long id, @Valid @RequestBody TreatmentDto dto) {
        TreatmentDto updatedDto = treatmentService.updateTreatment(id, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTreatment(@PathVariable Long id) {
        treatmentService.deleteTreatment(id);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Add/Remove methods for DoctorAppointment many-to-many table

    @PostMapping("/{treatmentId}/appointments/{appointmentId}")
    public ResponseEntity<TreatmentDto> addAppointment(@PathVariable Long treatmentId, @PathVariable Long appointmentId) {
        TreatmentDto updatedDto = treatmentService.addAppointment(treatmentId, appointmentId);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{treatmentId}/appointments/{appointmentId}")
    public ResponseEntity<TreatmentDto> removeAppointment(@PathVariable Long treatmentId, @PathVariable Long appointmentId) {
        TreatmentDto updatedDto = treatmentService.removeAppointment(treatmentId, appointmentId);

        return ResponseEntity.ok(updatedDto);
    }
}
