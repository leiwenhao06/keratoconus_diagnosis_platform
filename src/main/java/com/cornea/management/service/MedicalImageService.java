package com.cornea.management.service;

import com.cornea.management.dao.MedicalImageDAO;
import com.cornea.management.entity.MedicalImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MedicalImageService {

    private final MedicalImageDAO imageDAO = new MedicalImageDAO();
    private final PatientService patientService = new PatientService();

    public MedicalImage uploadImage(MedicalImage image) throws SQLException {
        if (!patientService.patientExists(image.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + image.getPatientId());
        }
        if (image.getImageType() == null) {
            throw new IllegalArgumentException("Image type is required");
        }
        int id = imageDAO.insert(image);
        return imageDAO.findById(id).orElseThrow();
    }

    public MedicalImage uploadImageFromFile(String patientId, String imageType, String eyeSide,
                                            String filePath, Integer examId) throws SQLException, IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }
        MedicalImage image = new MedicalImage();
        image.setPatientId(patientId);
        image.setExamId(examId);
        image.setImageType(imageType);
        image.setEyeSide(eyeSide);
        image.setImagePath(filePath);
        image.setFileName(path.getFileName().toString());
        image.setFileSize(Files.size(path));
        image.setImageData(Files.readAllBytes(path));
        return uploadImage(image);
    }

    public void deleteImage(int imageId) throws SQLException {
        imageDAO.delete(imageId);
    }

    public Optional<MedicalImage> getImageById(int imageId) throws SQLException {
        return imageDAO.findById(imageId);
    }

    public List<MedicalImage> getImagesByPatientId(String patientId) throws SQLException {
        return imageDAO.findByPatientId(patientId);
    }

    public List<MedicalImage> getImagesByExamId(int examId) throws SQLException {
        return imageDAO.findByExamId(examId);
    }
}
