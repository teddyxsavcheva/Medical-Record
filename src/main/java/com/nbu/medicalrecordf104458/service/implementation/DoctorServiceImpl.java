package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.mapper.DoctorMapper;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import com.nbu.medicalrecordf104458.service.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorMapper mapper;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;

    @Override
    public List<DoctorDto> getAllDoctors() {
        return List.of();
    }

    @Override
    public DoctorDto getDoctorById(Long id) {
        return null;
    }

    @Override
    public DoctorDto createDoctor(DoctorDto doctorDto) {
        return null;
    }

    @Override
    public DoctorDto updateDoctor(Long id, DoctorDto doctorDto) {
        return null;
    }

    @Override
    public void deleteDoctor(Long id) {

    }

    @Override
    public DoctorDto addSpecialization(Long doctorId, Long specializationId) {
        return null;
    }

    @Override
    public DoctorDto removeSpecialization(Long doctorId, Long specializationId) {
        return null;
    }
}
