package com.nbu.medicalrecordf104458.auth;

import com.nbu.medicalrecordf104458.config.JwtService;
import com.nbu.medicalrecordf104458.model.user.User;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository repository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDto register(UserDto request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .doctor(doctorRepository.findById(request.getDoctorId()).orElse(null))
                .patient(patientRepository.findById(request.getPatientId()).orElse(null))
                .build();

        if (user.getDoctor() != null && user.getPatient() != null) {
            throw new SecurityException("The User can't be a patient and a doctor at the same time!");
        }

        repository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

}
