package com.cornea.management.service;

import com.cornea.management.dao.PatientDAO;
import com.cornea.management.entity.Patient;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PatientService {

    private final PatientDAO patientDAO = new PatientDAO();

    public Patient registerPatient(Patient patient) throws SQLException {
        if (patient.getPatientId() == null || patient.getPatientId().isBlank()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        if (patient.getName() == null || patient.getName().isBlank()) {
            throw new IllegalArgumentException("Patient name cannot be empty");
        }
        if (patientDAO.exists(patient.getPatientId())) {
            throw new IllegalArgumentException("Patient ID already exists: " + patient.getPatientId());
        }
        patientDAO.insert(patient);
        return patient;
    }

    public Patient updatePatient(Patient patient) throws SQLException {
        if (!patientDAO.exists(patient.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + patient.getPatientId());
        }
        patientDAO.update(patient);
        return patient;
    }

    public void deletePatient(String patientId) throws SQLException {
        if (!patientDAO.exists(patientId)) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }
        patientDAO.delete(patientId);
    }

    public Optional<Patient> getPatientById(String patientId) throws SQLException {
        return patientDAO.findById(patientId);
    }

    public List<Patient> getAllPatients() throws SQLException {
        return patientDAO.findAll();
    }

    public List<Patient> searchPatientsByName(String keyword) throws SQLException {
        return patientDAO.searchByName(keyword);
    }

    public boolean patientExists(String patientId) throws SQLException {
        return patientDAO.exists(patientId);
    }
}
