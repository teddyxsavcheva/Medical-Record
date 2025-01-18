package com.nbu.medicalrecordf104458.controller;

import com.nbu.medicalrecordf104458.dto.SickLeaveDto;
import com.nbu.medicalrecordf104458.service.SickLeaveService;
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
@RequestMapping("/sick-leaves")
@RestController
public class SickLeaveController {

    private final SickLeaveService sickLeaveService;

    @GetMapping("/")
    public ResponseEntity<List<SickLeaveDto>> getAllSickLeaves() {
        return ResponseEntity.ok(sickLeaveService.getAllSickLeaves());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SickLeaveDto> getSickLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(sickLeaveService.getSickLeaveById(id));
    }

    @PostMapping("/")
    public ResponseEntity<SickLeaveDto> createSickLeave(@Valid @RequestBody SickLeaveDto sickLeaveDto) {
        SickLeaveDto createdDto = sickLeaveService.createSickLeave(sickLeaveDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SickLeaveDto> updateSickLeave(@PathVariable Long id, @Valid @RequestBody SickLeaveDto sickLeaveDto) {
        SickLeaveDto updatedDto = sickLeaveService.updateSickLeave(id, sickLeaveDto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSickLeave(@PathVariable Long id) {
        sickLeaveService.deleteSickLeave(id);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

}
