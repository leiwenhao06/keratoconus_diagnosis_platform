package com.cornea.management.service;

import com.cornea.management.dao.PatientDAO;
import com.cornea.management.entity.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final PatientDAO patientDAO;

    @Autowired
    public PatientService(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    @Transactional(rollbackFor = SQLException.class)
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
        log.info("Patient registered: {} ({})", patient.getName(), patient.getPatientId());
        return patient;
    }

    @Transactional(rollbackFor = SQLException.class)
    public Patient updatePatient(Patient patient) throws SQLException {
        int affected = patientDAO.update(patient);
        if (affected == 0) {
            throw new IllegalArgumentException("Patient not found: " + patient.getPatientId());
        }
        log.info("Patient updated: {}", patient.getPatientId());
        return patient;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void deletePatient(String patientId) throws SQLException {
        int affected = patientDAO.delete(patientId);
        if (affected == 0) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }
        log.info("Patient deleted: {}", patientId);
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
