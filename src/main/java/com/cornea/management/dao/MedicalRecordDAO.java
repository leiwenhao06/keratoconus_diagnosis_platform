package com.cornea.management.dao;

import com.cornea.management.entity.MedicalRecord;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MedicalRecordDAO {

    private final DataSource dataSource;

    public MedicalRecordDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int insert(MedicalRecord record) throws SQLException {
        if (record.getVisitDate() == null) {
            throw new IllegalArgumentException("Visit date is required");
        }
        String sql = "INSERT INTO medical_records (patient_id, visit_date, chief_complaint, " +
                "present_illness, past_history, physical_exam, diagnosis, left_eye_diagnosis, " +
                "right_eye_diagnosis, treatment_plan, doctor_notes, doctor_name) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, record.getPatientId());
            ps.setDate(2, Date.valueOf(record.getVisitDate()));
            ps.setString(3, record.getChiefComplaint());
            ps.setString(4, record.getPresentIllness());
            ps.setString(5, record.getPastHistory());
            ps.setString(6, record.getPhysicalExam());
            ps.setString(7, record.getDiagnosis());
            ps.setString(8, record.getLeftEyeDiagnosis());
            ps.setString(9, record.getRightEyeDiagnosis());
            ps.setString(10, record.getTreatmentPlan());
            ps.setString(11, record.getDoctorNotes());
            ps.setString(12, record.getDoctorName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to insert medical record");
    }

    public void update(MedicalRecord record) throws SQLException {
        if (record.getVisitDate() == null) {
            throw new IllegalArgumentException("Visit date is required for update");
        }
        String sql = "UPDATE medical_records SET visit_date=?, chief_complaint=?, present_illness=?, " +
                "past_history=?, physical_exam=?, diagnosis=?, left_eye_diagnosis=?, right_eye_diagnosis=?, " +
                "treatment_plan=?, doctor_notes=?, doctor_name=? WHERE record_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(record.getVisitDate()));
            ps.setString(2, record.getChiefComplaint());
            ps.setString(3, record.getPresentIllness());
            ps.setString(4, record.getPastHistory());
            ps.setString(5, record.getPhysicalExam());
            ps.setString(6, record.getDiagnosis());
            ps.setString(7, record.getLeftEyeDiagnosis());
            ps.setString(8, record.getRightEyeDiagnosis());
            ps.setString(9, record.getTreatmentPlan());
            ps.setString(10, record.getDoctorNotes());
            ps.setString(11, record.getDoctorName());
            ps.setInt(12, record.getRecordId());
            ps.executeUpdate();
        }
    }

    public void delete(int recordId) throws SQLException {
        String sql = "DELETE FROM medical_records WHERE record_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ps.executeUpdate();
        }
    }

    public Optional<MedicalRecord> findById(int recordId) throws SQLException {
        String sql = "SELECT * FROM medical_records WHERE record_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRow(rs));
            }
        }
    }

    public List<MedicalRecord> findByPatientId(String patientId) throws SQLException {
        String sql = "SELECT * FROM medical_records WHERE patient_id=? ORDER BY visit_date DESC";
        List<MedicalRecord> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<MedicalRecord> findByDateRange(LocalDate from, LocalDate to) throws SQLException {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Date range 'from' and 'to' are required");
        }
        String sql = "SELECT mr.*, p.name FROM medical_records mr JOIN patients p ON mr.patient_id=p.patient_id " +
                "WHERE mr.visit_date BETWEEN ? AND ? ORDER BY mr.visit_date DESC";
        List<MedicalRecord> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private MedicalRecord mapRow(ResultSet rs) throws SQLException {
        MedicalRecord r = new MedicalRecord();
        r.setRecordId(rs.getInt("record_id"));
        r.setPatientId(rs.getString("patient_id"));
        r.setVisitDate(rs.getDate("visit_date").toLocalDate());
        r.setChiefComplaint(rs.getString("chief_complaint"));
        r.setPresentIllness(rs.getString("present_illness"));
        r.setPastHistory(rs.getString("past_history"));
        r.setPhysicalExam(rs.getString("physical_exam"));
        r.setDiagnosis(rs.getString("diagnosis"));
        r.setLeftEyeDiagnosis(rs.getString("left_eye_diagnosis"));
        r.setRightEyeDiagnosis(rs.getString("right_eye_diagnosis"));
        r.setTreatmentPlan(rs.getString("treatment_plan"));
        r.setDoctorNotes(rs.getString("doctor_notes"));
        r.setDoctorName(rs.getString("doctor_name"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) r.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) r.setUpdatedAt(ua.toLocalDateTime());
        return r;
    }
}
