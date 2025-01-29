package com.nbu.medicalrecordf104458.auth;

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
    public ResponseEntity<AuthenticationResponseDto> register(
            @RequestBody UserDto request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> register(
            @RequestBody AuthenticationRequestDto request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

}
