package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.auth.AuthenticationRequestDto;
import com.nbu.medicalrecordf104458.dto.auth.AuthenticationResponseDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterDoctorDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterGpDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterPatientDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterUserDto;
import com.nbu.medicalrecordf104458.config.JwtService;
import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.dto.GeneralPractitionerDto;
import com.nbu.medicalrecordf104458.dto.PatientDto;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.user.Role;
import com.nbu.medicalrecordf104458.model.user.User;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.GeneralPractitionerRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.repository.user.UserRepository;
import com.nbu.medicalrecordf104458.service.DoctorService;
import com.nbu.medicalrecordf104458.service.GeneralPractitionerService;
import com.nbu.medicalrecordf104458.service.PatientService;
import com.nbu.medicalrecordf104458.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final DoctorService doctorService;
    private final GeneralPractitionerService gpService;
    private final PatientService patientService;

    private final DoctorRepository doctorRepository;
    private final GeneralPractitionerRepository gPRepository;
    private final PatientRepository patientRepository;

    @Override
    public AuthenticationResponseDto registerAdmin(RegisterUserDto userDto) {
        User user = User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole())
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponseDto registerDoctor(RegisterDoctorDto registerDoctorDto) {
        DoctorDto doctorToSave = new DoctorDto();
        doctorToSave.setName(registerDoctorDto.getName());
        doctorToSave.setSpecializationIds(registerDoctorDto.getSpecializationIds());

        DoctorDto savedDoctor = doctorService.createDoctor(doctorToSave);
        Doctor savedEntity = doctorRepository.findById(savedDoctor.getId())
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + savedDoctor.getId()));

        User user = User.builder()
                .email(registerDoctorDto.getEmail())
                .password(passwordEncoder.encode(registerDoctorDto.getPassword()))
                .role(Role.DOCTOR)
                .doctor(savedEntity)
                .patient(null)
                .build();

        savedEntity.setUser(user);

        userRepository.save(user);
        doctorRepository.save(savedEntity);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponseDto registerGp(RegisterGpDto registerGpDto) {
        GeneralPractitionerDto gpToSave = new GeneralPractitionerDto();
        gpToSave.setDoctor(registerGpDto.getDoctor());
        gpToSave.setPatients(registerGpDto.getPatients());

        GeneralPractitionerDto savedGp = gpService.createDoctor(gpToSave);
        GeneralPractitioner savedEntity = gPRepository.findById(savedGp.getDoctor().getId())
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + savedGp.getDoctor().getId()));

        User user = User.builder()
                .email(registerGpDto.getEmail())
                .password(passwordEncoder.encode(registerGpDto.getPassword()))
                .role(Role.DOCTOR)
                .doctor(savedEntity)
                .patient(null)
                .build();

        savedEntity.setUser(user);

        userRepository.save(user);
        gPRepository.save(savedEntity);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponseDto registerPatient(RegisterPatientDto registerPatientDto) {
        PatientDto patientToSave = new PatientDto();
        patientToSave.setName(registerPatientDto.getName());
        patientToSave.setUnifiedCivilNumber(registerPatientDto.getUnifiedCivilNumber());
        patientToSave.setLastInsurancePayment(registerPatientDto.getLastInsurancePayment());
        patientToSave.setFamilyDoctorId(registerPatientDto.getFamilyDoctorId());

        PatientDto savedPatient = patientService.createPatient(patientToSave);
        Patient savedEntity = patientRepository.findById(savedPatient.getId())
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + savedPatient.getId()));

        User user = User.builder()
                .email(registerPatientDto.getEmail())
                .password(passwordEncoder.encode(registerPatientDto.getPassword()))
                .role(Role.PATIENT)
                .doctor(null)
                .patient(savedEntity)
                .build();

        savedEntity.setUser(user);

        userRepository.save(user);
        patientRepository.save(savedEntity);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

}