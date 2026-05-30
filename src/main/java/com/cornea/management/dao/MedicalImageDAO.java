package com.cornea.management.dao;

import com.cornea.management.config.DatabaseConfig;
import com.cornea.management.entity.MedicalImage;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MedicalImageDAO {

    public int insert(MedicalImage image) throws SQLException {
        String sql = "INSERT INTO medical_images (patient_id, exam_id, image_type, eye_side, " +
                "image_path, image_data, file_name, file_size) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, image.getPatientId());
            if (image.getExamId() != null) ps.setInt(2, image.getExamId());
            else ps.setNull(2, Types.INTEGER);
            ps.setString(3, image.getImageType());
            ps.setString(4, image.getEyeSide());
            ps.setString(5, image.getImagePath());
            if (image.getImageData() != null) {
                ps.setBlob(6, new javax.sql.rowset.serial.SerialBlob(image.getImageData()));
            } else {
                ps.setNull(6, Types.BLOB);
            }
            ps.setString(7, image.getFileName());
            if (image.getFileSize() != null) ps.setLong(8, image.getFileSize());
            else ps.setNull(8, Types.BIGINT);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to insert medical image");
    }

    public void updatePath(int imageId, String path) throws SQLException {
        String sql = "UPDATE medical_images SET image_path=? WHERE image_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setInt(2, imageId);
            ps.executeUpdate();
        }
    }

    public void delete(int imageId) throws SQLException {
        String sql = "DELETE FROM medical_images WHERE image_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, imageId);
            ps.executeUpdate();
        }
    }

    public Optional<MedicalImage> findById(int imageId) throws SQLException {
        String sql = "SELECT * FROM medical_images WHERE image_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, imageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRow(rs));
            }
        }
    }

    public List<MedicalImage> findByPatientId(String patientId) throws SQLException {
        String sql = "SELECT * FROM medical_images WHERE patient_id=? ORDER BY upload_date DESC";
        List<MedicalImage> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<MedicalImage> findByExamId(int examId) throws SQLException {
        String sql = "SELECT * FROM medical_images WHERE exam_id=? ORDER BY upload_date DESC";
        List<MedicalImage> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private MedicalImage mapRow(ResultSet rs) throws SQLException {
        MedicalImage img = new MedicalImage();
        img.setImageId(rs.getInt("image_id"));
        img.setPatientId(rs.getString("patient_id"));
        int examId = rs.getInt("exam_id");
        if (!rs.wasNull()) img.setExamId(examId);
        img.setImageType(rs.getString("image_type"));
        img.setEyeSide(rs.getString("eye_side"));
        img.setImagePath(rs.getString("image_path"));
        Blob blob = rs.getBlob("image_data");
        if (blob != null) {
            img.setImageData(blob.getBytes(1, (int) blob.length()));
        }
        img.setFileName(rs.getString("file_name"));
        long fileSize = rs.getLong("file_size");
        if (!rs.wasNull()) img.setFileSize(fileSize);
        Timestamp ud = rs.getTimestamp("upload_date");
        if (ud != null) img.setUploadDate(ud.toLocalDateTime());
        return img;
    }
}
