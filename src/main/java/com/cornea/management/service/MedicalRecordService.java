package com.cornea.management.service;

import com.cornea.management.dao.MedicalRecordDAO;
import com.cornea.management.entity.MedicalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordService.class);

    private final MedicalRecordDAO recordDAO;
    private final PatientService patientService;

    @Autowired
    public MedicalRecordService(MedicalRecordDAO recordDAO, PatientService patientService) {
        this.recordDAO = recordDAO;
        this.patientService = patientService;
    }

    @Transactional(rollbackFor = SQLException.class)
    public MedicalRecord createRecord(MedicalRecord record) throws SQLException {
        if (!patientService.patientExists(record.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + record.getPatientId());
        }
        if (record.getVisitDate() == null) {
            throw new IllegalArgumentException("Visit date is required");
        }
        int id = recordDAO.insert(record);
        log.info("Medical record created: recordId={}, patientId={}", id, record.getPatientId());
        return recordDAO.findById(id)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve created record: " + id));
    }

    @Transactional(rollbackFor = SQLException.class)
    public MedicalRecord updateRecord(MedicalRecord record) throws SQLException {
        if (record.getVisitDate() == null) {
            throw new IllegalArgumentException("Visit date is required");
        }
        recordDAO.update(record);
        log.info("Medical record updated: recordId={}", record.getRecordId());
        return recordDAO.findById(record.getRecordId())
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve updated record: " + record.getRecordId()));
    }

    @Transactional(rollbackFor = SQLException.class)
    public void deleteRecord(int recordId) throws SQLException {
        recordDAO.delete(recordId);
        log.info("Medical record deleted: recordId={}", recordId);
    }

    public Optional<MedicalRecord> getRecordById(int recordId) throws SQLException {
        return recordDAO.findById(recordId);
    }

    public List<MedicalRecord> getRecordsByPatientId(String patientId) throws SQLException {
        return recordDAO.findByPatientId(patientId);
    }

    public List<MedicalRecord> getRecordsByDateRange(LocalDate from, LocalDate to) throws SQLException {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Date range 'from' and 'to' are required");
        }
        return recordDAO.findByDateRange(from, to);
    }
}
