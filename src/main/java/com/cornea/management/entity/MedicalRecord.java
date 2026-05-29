package com.cornea.management.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicalRecord {

    private Integer recordId;
    private String patientId;
    private LocalDate visitDate;
    private String chiefComplaint;
    private String presentIllness;
    private String pastHistory;
    private String physicalExam;
    private String diagnosis;
    private String leftEyeDiagnosis;
    private String rightEyeDiagnosis;
    private String treatmentPlan;
    private String doctorNotes;
    private String doctorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MedicalRecord() {}

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }

    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }

    public String getPresentIllness() { return presentIllness; }
    public void setPresentIllness(String presentIllness) { this.presentIllness = presentIllness; }

    public String getPastHistory() { return pastHistory; }
    public void setPastHistory(String pastHistory) { this.pastHistory = pastHistory; }

    public String getPhysicalExam() { return physicalExam; }
    public void setPhysicalExam(String physicalExam) { this.physicalExam = physicalExam; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getLeftEyeDiagnosis() { return leftEyeDiagnosis; }
    public void setLeftEyeDiagnosis(String leftEyeDiagnosis) { this.leftEyeDiagnosis = leftEyeDiagnosis; }

    public String getRightEyeDiagnosis() { return rightEyeDiagnosis; }
    public void setRightEyeDiagnosis(String rightEyeDiagnosis) { this.rightEyeDiagnosis = rightEyeDiagnosis; }

    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }

    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return String.format("MedicalRecord[id=%d, patient=%s, date=%s, doctor=%s]",
                recordId, patientId, visitDate, doctorName);
    }
}
