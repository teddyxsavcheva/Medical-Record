package com.nbu.medicalrecordf104458.controller;

import com.nbu.medicalrecordf104458.dto.queries.DoctorAppointmentsCountDto;
import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.service.DoctorService;
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
@RequestMapping("/doctors")
@RestController
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/")
    public ResponseEntity<Set<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PostMapping("/")
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody DoctorDto dto) {
        DoctorDto createdDto = doctorService.createDoctor(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorDto dto) {
        DoctorDto updatedDto = doctorService.updateDoctor(id, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Add/Remove methods for Specialization many-to-many table

    @PostMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<DoctorDto> addSpecialization(@PathVariable Long doctorId, @PathVariable Long specializationId) {
        DoctorDto updatedDto = doctorService.addSpecialization(doctorId, specializationId);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<DoctorDto> removeSpecialization(@PathVariable Long doctorId, @PathVariable Long specializationId) {
        DoctorDto updatedDto = doctorService.removeSpecialization(doctorId, specializationId);

        return ResponseEntity.ok(updatedDto);
    }

    // Queries
    @GetMapping("/appointments-count")
    public ResponseEntity<Set<DoctorAppointmentsCountDto>> getAllDoctorsWithAppointmentCount() {
        return ResponseEntity.ok(doctorService.getAllDoctorsWithAppointmentCount());
    }

    @GetMapping("/{id}/appointments-count")
    public ResponseEntity<DoctorAppointmentsCountDto> getDoctorWithAppointmentCount(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorWithAppointmentCount(id));
    }

    @GetMapping("/doctors-with-most-sick-leaves")
    public ResponseEntity<Set<DoctorDto>> getDoctorsWithMostSickLeaves() {
        Set<DoctorDto> doctors = doctorService.findDoctorsWithMostSickLeaves();
        return ResponseEntity.ok(doctors);
    }

}
