package com.cornea.management.service;

import com.cornea.management.dao.MedicalRecordDAO;
import com.cornea.management.entity.MedicalRecord;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MedicalRecordService {

    private final MedicalRecordDAO recordDAO = new MedicalRecordDAO();
    private final PatientService patientService = new PatientService();

    public MedicalRecord createRecord(MedicalRecord record) throws SQLException {
        if (!patientService.patientExists(record.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + record.getPatientId());
        }
        if (record.getVisitDate() == null) {
            throw new IllegalArgumentException("Visit date is required");
        }
        int id = recordDAO.insert(record);
        return recordDAO.findById(id).orElseThrow();
    }

    public MedicalRecord updateRecord(MedicalRecord record) throws SQLException {
        recordDAO.update(record);
        return recordDAO.findById(record.getRecordId()).orElseThrow();
    }

    public void deleteRecord(int recordId) throws SQLException {
        recordDAO.delete(recordId);
    }

    public Optional<MedicalRecord> getRecordById(int recordId) throws SQLException {
        return recordDAO.findById(recordId);
    }

    public List<MedicalRecord> getRecordsByPatientId(String patientId) throws SQLException {
        return recordDAO.findByPatientId(patientId);
    }

    public List<MedicalRecord> getRecordsByDateRange(LocalDate from, LocalDate to) throws SQLException {
        return recordDAO.findByDateRange(from, to);
    }
}
