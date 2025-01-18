package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.SickLeaveDto;

import java.util.List;

public interface SickLeaveService {

    List<SickLeaveDto> getAllSickLeaves();

    SickLeaveDto getSickLeaveById(Long id);

    SickLeaveDto createSickLeave(SickLeaveDto sickLeaveDto);

    SickLeaveDto updateSickLeave(Long id, SickLeaveDto sickLeaveDto);

    void deleteSickLeave(Long id);

}
