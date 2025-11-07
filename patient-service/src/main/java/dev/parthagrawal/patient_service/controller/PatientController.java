package dev.parthagrawal.patient_service.controller;

import dev.parthagrawal.patient_service.dto.PatientRequestDto;
import dev.parthagrawal.patient_service.dto.PatientResponseDto;
import dev.parthagrawal.patient_service.service.PatientService;
import dev.parthagrawal.patient_service.validators.CreatePatientValidationGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "APIs for managing patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get patients", description = "Retrieve a list of patients")
    public ResponseEntity<List<PatientResponseDto>> getPatients(){
        List<PatientResponseDto> patients = patientService.getPatients();
        return ResponseEntity.ok(patients);
    }

    @PostMapping
    @Operation(summary = "Create patient", description = "Create a new patient")
    public ResponseEntity<PatientResponseDto> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDto patientRequestDto){
        PatientResponseDto createdPatient = patientService.createPatient(patientRequestDto);
        return ResponseEntity.ok(createdPatient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update patient", description = "Update an existing patient")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable String id,
            @Validated({Default.class}) @RequestBody PatientRequestDto patientRequestDto){
        PatientResponseDto updatedPatient = patientService.updatePatient(java.util.UUID.fromString(id), patientRequestDto);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient", description = "Delete a patient by ID")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

}
