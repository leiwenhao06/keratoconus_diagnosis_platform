package com.cornea.management.dao;

import com.cornea.management.config.DatabaseConfig;
import com.cornea.management.entity.Patient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientDAO {

    public void insert(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (patient_id, name, gender, date_of_birth, age, " +
                "id_card, contact, address, medical_history) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getPatientId());
            ps.setString(2, patient.getName());
            ps.setString(3, patient.getGender());
            ps.setDate(4, patient.getDateOfBirth() != null ? Date.valueOf(patient.getDateOfBirth()) : null);
            if (patient.getAge() != null) ps.setInt(5, patient.getAge());
            else ps.setNull(5, Types.INTEGER);
            ps.setString(6, patient.getIdCard());
            ps.setString(7, patient.getContact());
            ps.setString(8, patient.getAddress());
            ps.setString(9, patient.getMedicalHistory());
            ps.executeUpdate();
        }
    }

    public int update(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET name=?, gender=?, date_of_birth=?, age=?, " +
                "id_card=?, contact=?, address=?, medical_history=? WHERE patient_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setString(2, patient.getGender());
            ps.setDate(3, patient.getDateOfBirth() != null ? Date.valueOf(patient.getDateOfBirth()) : null);
            if (patient.getAge() != null) ps.setInt(4, patient.getAge());
            else ps.setNull(4, Types.INTEGER);
            ps.setString(5, patient.getIdCard());
            ps.setString(6, patient.getContact());
            ps.setString(7, patient.getAddress());
            ps.setString(8, patient.getMedicalHistory());
            ps.setString(9, patient.getPatientId());
            return ps.executeUpdate();
        }
    }

    public int delete(String patientId) throws SQLException {
        String sql = "DELETE FROM patients WHERE patient_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            return ps.executeUpdate();
        }
    }

    public Optional<Patient> findById(String patientId) throws SQLException {
        String sql = "SELECT * FROM patients WHERE patient_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    public List<Patient> findAll() throws SQLException {
        String sql = "SELECT * FROM patients ORDER BY created_at DESC";
        List<Patient> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Patient> searchByName(String keyword) throws SQLException {
        String sql = "SELECT * FROM patients WHERE name LIKE ? ORDER BY created_at DESC";
        List<Patient> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean exists(String patientId) throws SQLException {
        String sql = "SELECT 1 FROM patients WHERE patient_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getString("patient_id"));
        p.setName(rs.getString("name"));
        p.setGender(rs.getString("gender"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) p.setDateOfBirth(dob.toLocalDate());
        int age = rs.getInt("age");
        if (!rs.wasNull()) p.setAge(age);
        p.setIdCard(rs.getString("id_card"));
        p.setContact(rs.getString("contact"));
        p.setAddress(rs.getString("address"));
        p.setMedicalHistory(rs.getString("medical_history"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) p.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) p.setUpdatedAt(ua.toLocalDateTime());
        return p;
    }
}
