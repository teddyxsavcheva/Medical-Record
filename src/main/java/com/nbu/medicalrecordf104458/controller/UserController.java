package com.nbu.medicalrecordf104458.controller;

import com.nbu.medicalrecordf104458.dto.auth.AuthenticationRequestDto;
import com.nbu.medicalrecordf104458.dto.auth.AuthenticationResponseDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterDoctorDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterGpDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterPatientDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterUserDto;
import com.nbu.medicalrecordf104458.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@Valid @RequestBody RegisterUserDto request) {
        return ResponseEntity.ok(userService.registerAdmin(request));
    }

    @PostMapping("/register-doctor")
    public ResponseEntity<AuthenticationResponseDto> registerDoctor(@Valid @RequestBody RegisterDoctorDto request) {
        return ResponseEntity.ok(userService.registerDoctor(request));
    }

    @PostMapping("/register-gp")
    public ResponseEntity<AuthenticationResponseDto> registerGp(@Valid @RequestBody RegisterGpDto request) {
        return ResponseEntity.ok(userService.registerGp(request));
    }

    @PostMapping("/register-patient")
    public ResponseEntity<AuthenticationResponseDto> registerPatient(@Valid @RequestBody RegisterPatientDto request) {
        return ResponseEntity.ok(userService.registerPatient(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }

    @GetMapping("/users")
    public ResponseEntity<Set<RegisterUserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
