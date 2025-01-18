package com.nbu.medicalrecordf104458.controller;

import com.nbu.medicalrecordf104458.dto.SpecializationDto;
import com.nbu.medicalrecordf104458.service.SpecializationService;
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

import java.util.List;

@AllArgsConstructor
@RequestMapping("/specializations")
@RestController
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping("/")
    public ResponseEntity<List<SpecializationDto>> getAllSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecializationDto> getSpecializationById(@PathVariable Long id) {
        return ResponseEntity.ok(specializationService.getSpecializationById(id));
    }

    @PostMapping("/")
    public ResponseEntity<SpecializationDto> createSpecialization(@Valid @RequestBody SpecializationDto dto) {
        SpecializationDto createdDto = specializationService.createSpecialization(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecializationDto> updateSpecialization(@PathVariable Long id, @Valid @RequestBody SpecializationDto dto) {
        SpecializationDto updatedDto = specializationService.updateSpecialization(id, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteSpecialization(id);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Add/Remove methods for Doctor many-to-many table

    @PostMapping("/{specializationId}/doctors/{doctorId}")
    public ResponseEntity<SpecializationDto> addDoctor(@PathVariable Long specializationId, @PathVariable Long doctorId) {
        SpecializationDto updatedDto = specializationService.addDoctor(specializationId, doctorId);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("{/specializationId}/doctors/{doctorId}")
    public ResponseEntity<SpecializationDto> removeDoctor(@PathVariable Long specializationId, @PathVariable Long doctorId) {
        SpecializationDto updatedDto = specializationService.removeDoctor(specializationId, doctorId);

        return ResponseEntity.ok(updatedDto);
    }
}
