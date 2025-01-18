package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.SickLeaveDto;
import com.nbu.medicalrecordf104458.mapper.SickLeaveMapper;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.repository.SickLeaveRepository;
import com.nbu.medicalrecordf104458.service.SickLeaveService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SickLeaveServiceImpl implements SickLeaveService {

    private final SickLeaveMapper mapper;
    private final SickLeaveRepository repository;


    @Override
    public List<SickLeaveDto> getAllSickLeaves() {
        return repository.findAll().stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SickLeaveDto getSickLeaveById(Long id) {
        SickLeave sickLeave = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No sick leave found with id: " + id));

        return mapper.convertToDto(sickLeave);
    }

    @Override
    public SickLeaveDto createSickLeave(SickLeaveDto sickLeaveDto) {
        SickLeave sickLeave = mapper.convertToEntity(sickLeaveDto);

        return mapper.convertToDto(repository.save(sickLeave));
    }

    @Override
    public SickLeaveDto updateSickLeave(Long id, SickLeaveDto sickLeaveDto) {
        SickLeave sickLeave = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No sick leave found with id: " + id));

        sickLeave.setStartDate(sickLeaveDto.getStartDate());
        sickLeave.setEndDate(sickLeaveDto.getEndDate());

        return mapper.convertToDto(repository.save(sickLeave));
    }

    @Override
    public void deleteSickLeave(Long id) {
        SickLeave sickLeave = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No sick leave found with id: " + id));

        repository.delete(sickLeave);
    }

}
