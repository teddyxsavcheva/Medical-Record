package com.nbu.medicalrecordf104458.utility;

import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.dto.GeneralPractitionerDto;
import com.nbu.medicalrecordf104458.dto.PatientDto;

public class UserDtoValidator {

    private UserDtoValidator() {
        // Private constructor to prevent class instantiation
    }

    public static boolean isValidRole(DoctorDto doctorDto, GeneralPractitionerDto gpDto, PatientDto patientDto) {
        int count = 0;
        if (doctorDto != null) count++;
        if (gpDto != null) count++;
        if (patientDto != null) count++;

        return count == 1;
    }

}
