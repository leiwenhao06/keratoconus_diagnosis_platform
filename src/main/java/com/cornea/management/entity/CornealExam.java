package com.cornea.management.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CornealExam {

    private Integer examId;
    private String patientId;
    private LocalDate examDate;
    private String eyeSide;
    private String examType;
    private String diagnosis;
    private LocalDateTime createdAt;

    private CornealTopography topography;
    private BiomechanicalParams biomechanics;

    public CornealExam() {}

    public Integer getExamId() { return examId; }
    public void setExamId(Integer examId) { this.examId = examId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }

    public String getEyeSide() { return eyeSide; }
    public void setEyeSide(String eyeSide) { this.eyeSide = eyeSide; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public CornealTopography getTopography() { return topography; }
    public void setTopography(CornealTopography topography) { this.topography = topography; }

    public BiomechanicalParams getBiomechanics() { return biomechanics; }
    public void setBiomechanics(BiomechanicalParams biomechanics) { this.biomechanics = biomechanics; }

    @Override
    public String toString() {
        return String.format("CornealExam[id=%d, patient=%s, date=%s, eye=%s, type=%s]",
                examId, patientId, examDate, eyeSide, examType);
    }
}
