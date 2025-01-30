package com.nbu.medicalrecordf104458.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@Valid @RequestBody RegisterUserDto request) {
        return ResponseEntity.ok(service.registerAdmin(request));
    }

    @PostMapping("/register-doctor")
    public ResponseEntity<AuthenticationResponseDto> registerDoctor(@Valid @RequestBody RegisterDoctorDto request) {
        return ResponseEntity.ok(service.registerDoctor(request));
    }

    @PostMapping("/register-gp")
    public ResponseEntity<AuthenticationResponseDto> registerGp(@Valid @RequestBody RegisterGpDto request) {
        return ResponseEntity.ok(service.registerGp(request));
    }

    @PostMapping("/register-patient")
    public ResponseEntity<AuthenticationResponseDto> registerPatient(@Valid @RequestBody RegisterPatientDto request) {
        return ResponseEntity.ok(service.registerPatient(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

}
