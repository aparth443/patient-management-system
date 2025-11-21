package dev.parthagrawal.patient_service.service;

import dev.parthagrawal.patient_service.dto.PatientRequestDto;
import dev.parthagrawal.patient_service.dto.PatientResponseDto;
import dev.parthagrawal.patient_service.exception.EmailAlreadyExistsException;
import dev.parthagrawal.patient_service.exception.PatientNotFoundException;
import dev.parthagrawal.patient_service.grpc.BillingServiceGrpcClient;
import dev.parthagrawal.patient_service.kafka.KafkaProducer;
import dev.parthagrawal.patient_service.mapper.PatientMapper;
import dev.parthagrawal.patient_service.model.Patient;
import dev.parthagrawal.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDto> getPatients() {
       List<Patient> patients = patientRepository.findAll();
       return patients.stream().map(PatientMapper::toDto).toList();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        if(patientRepository.existsByEmail(patientRequestDto.getEmail())){
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDto.getEmail());
        }
        Patient patient = PatientMapper.toModel(patientRequestDto);
        Patient savedPatient = patientRepository.save(patient);
        billingServiceGrpcClient.createBillingAccount(savedPatient.getId().toString(), savedPatient.getName(), savedPatient.getEmail());
        kafkaProducer.sendEvent(savedPatient);
        return PatientMapper.toDto(savedPatient);
    }

    public PatientResponseDto updatePatient(UUID id, PatientRequestDto patientRequestDto) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        if(patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)){
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDto.getEmail());
        }
        patient.setName(patientRequestDto.getName());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));
        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
