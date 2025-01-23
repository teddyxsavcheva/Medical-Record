package com.nbu.medicalrecordf104458.controller;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.DiagnoseDto;
import com.nbu.medicalrecordf104458.service.DoctorAppointmentService;
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
@RequestMapping("/doctor-appointments")
@RestController
public class DoctorAppointmentController {

    private final DoctorAppointmentService appointmentService;

    @GetMapping("/")
    public ResponseEntity<Set<AppointmentDto>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping("/")
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody AppointmentDto appointmentDto) {
        AppointmentDto createdAppointment = appointmentService.createAppointment(appointmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(@PathVariable Long id,
                                                            @Valid @RequestBody AppointmentDto appointmentDto) {
        AppointmentDto updatedAppointment = appointmentService.updateAppointment(id, appointmentDto);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Add/Remove methods for Diagnose many-to-many table

    @PostMapping("/{appointmentId}/diagnoses/{diagnoseId}")
    public ResponseEntity<AppointmentDto> addDiagnose(@PathVariable Long appointmentId, @PathVariable Long diagnoseId) {
        AppointmentDto updatedAppointment = appointmentService.addDiagnose(appointmentId, diagnoseId);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{appointmentId}/diagnoses/{diagnoseId}")
    public ResponseEntity<AppointmentDto> removeDiagnose(@PathVariable Long appointmentId, @PathVariable Long diagnoseId) {
        AppointmentDto updatedAppointment = appointmentService.removeDiagnose(appointmentId, diagnoseId);
        return ResponseEntity.ok(updatedAppointment);
    }

    // Add/Remove methods for Treatment many-to-many table

    @PostMapping("/{appointmentId}/treatments/{treatmentId}")
    public ResponseEntity<AppointmentDto> addTreatment(@PathVariable Long appointmentId, @PathVariable Long treatmentId) {
        AppointmentDto updatedAppointment = appointmentService.addTreatment(appointmentId, treatmentId);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{appointmentId}/treatments/{treatmentId}")
    public ResponseEntity<AppointmentDto> removeTreatment(@PathVariable Long appointmentId, @PathVariable Long treatmentId) {
        AppointmentDto updatedAppointment = appointmentService.removeTreatment(appointmentId, treatmentId);
        return ResponseEntity.ok(updatedAppointment);
    }

    // Queries

    @GetMapping("/most-common-diagnoses")
    public Set<DiagnoseDto> getMostCommonDiagnoses() {
        return appointmentService.findMostCommonDiagnoses();
    }

}
