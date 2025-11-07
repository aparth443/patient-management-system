package dev.parthagrawal.patient_service.mapper;

import dev.parthagrawal.patient_service.dto.PatientRequestDto;
import dev.parthagrawal.patient_service.dto.PatientResponseDto;
import dev.parthagrawal.patient_service.model.Patient;

import java.time.LocalDate;
import java.util.UUID;

public class PatientMapper {
    public static PatientResponseDto toDto(Patient patient) {
        PatientResponseDto dto = new PatientResponseDto();
        dto.setId(patient.getId().toString());
        dto.setName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setAddress(patient.getAddress());
        dto.setDateOfBirth(patient.getDateOfBirth().toString());
        return dto;
    }

    public static Patient toModel(PatientRequestDto dto) {
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(dto.getRegisteredDate()));
        return patient;
    }
}
