package com.nbu.medicalrecordf104458.auth;

import com.nbu.medicalrecordf104458.config.JwtService;
import com.nbu.medicalrecordf104458.mapper.DoctorMapper;
import com.nbu.medicalrecordf104458.mapper.GeneralPractitionerMapper;
import com.nbu.medicalrecordf104458.mapper.PatientMapper;
import com.nbu.medicalrecordf104458.model.user.Role;
import com.nbu.medicalrecordf104458.model.user.User;
import com.nbu.medicalrecordf104458.repository.user.UserRepository;
import com.nbu.medicalrecordf104458.service.DoctorService;
import com.nbu.medicalrecordf104458.service.GeneralPractitionerService;
import com.nbu.medicalrecordf104458.service.PatientService;
import com.nbu.medicalrecordf104458.utility.UserDtoValidator;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DoctorMapper doctorMapper;
    private final GeneralPractitionerMapper gpMapper;
    private final GeneralPractitionerService gpService;
    private final PatientMapper patientMapper;

    public AuthenticationResponseDto register(UserDto userDto) {
        if (!UserDtoValidator.isValidRole(userDto.getDoctorDto(), userDto.getGpDto(), userDto.getPatientDto())) {
            throw new SecurityException("The User can't be associated with more than one entity!");
        }

        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole())
                .build();

        if (userDto.getRole() == Role.DOCTOR) {
            if (userDto.getDoctorDto() != null) {
                user.setDoctor(doctorMapper.convertToEntity(doctorService.createDoctor(userDto.getDoctorDto())));
            } else if (userDto.getGpDto() != null) {
                user.setDoctor(gpMapper.convertToEntity(gpService.createDoctor(userDto.getGpDto())));
            }
        }

        else if (userDto.getRole() == Role.PATIENT) {
            user.setPatient(patientMapper.convertToEntity(patientService.createPatient(userDto.getPatientDto())));
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
