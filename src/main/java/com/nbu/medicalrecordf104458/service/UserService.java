package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.auth.AuthenticationRequestDto;
import com.nbu.medicalrecordf104458.dto.auth.AuthenticationResponseDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterDoctorDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterGpDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterPatientDto;
import com.nbu.medicalrecordf104458.dto.auth.RegisterUserDto;

public interface UserService {

    AuthenticationResponseDto registerAdmin(RegisterUserDto userDto);

    AuthenticationResponseDto registerDoctor(RegisterDoctorDto registerDoctorDto);

    AuthenticationResponseDto registerGp(RegisterGpDto registerGpDto);

    AuthenticationResponseDto registerPatient(RegisterPatientDto registerPatientDto);

    AuthenticationResponseDto authenticate(AuthenticationRequestDto request);

}
