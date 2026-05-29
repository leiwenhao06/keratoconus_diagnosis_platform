package com.cornea.management.entity;

import java.time.LocalDateTime;

public class MedicalImage {

    private Integer imageId;
    private String patientId;
    private Integer examId;
    private String imageType;
    private String eyeSide;
    private String imagePath;
    private byte[] imageData;
    private String fileName;
    private Long fileSize;
    private LocalDateTime uploadDate;

    public MedicalImage() {}

    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public Integer getExamId() { return examId; }
    public void setExamId(Integer examId) { this.examId = examId; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public String getEyeSide() { return eyeSide; }
    public void setEyeSide(String eyeSide) { this.eyeSide = eyeSide; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    @Override
    public String toString() {
        return String.format("MedicalImage[id=%d, type=%s, eye=%s, file=%s]",
                imageId, imageType, eyeSide, fileName);
    }
}
