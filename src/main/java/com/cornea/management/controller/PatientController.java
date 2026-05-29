package com.cornea.management.controller;

import com.cornea.management.dto.ApiResponse;
import com.cornea.management.entity.Patient;
import com.cornea.management.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService = new PatientService();

    @PostMapping
    public ApiResponse<Patient> create(@RequestBody Patient patient) throws SQLException {
        return ApiResponse.success("Patient created", patientService.registerPatient(patient));
    }

    @GetMapping("/{id}")
    public ApiResponse<Patient> getById(@PathVariable String id) throws SQLException {
        Optional<Patient> patient = patientService.getPatientById(id);
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient not found: " + id);
        }
        return ApiResponse.success(patient.get());
    }

    @GetMapping
    public ApiResponse<List<Patient>> list(@RequestParam(required = false) String name) throws SQLException {
        if (name != null && !name.isBlank()) {
            return ApiResponse.success(patientService.searchPatientsByName(name));
        }
        return ApiResponse.success(patientService.getAllPatients());
    }

    @PutMapping("/{id}")
    public ApiResponse<Patient> update(@PathVariable String id, @RequestBody Patient patient) throws SQLException {
        patient.setPatientId(id);
        return ApiResponse.success("Patient updated", patientService.updatePatient(patient));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) throws SQLException {
        patientService.deletePatient(id);
        return ApiResponse.success("Patient deleted", null);
    }
}
