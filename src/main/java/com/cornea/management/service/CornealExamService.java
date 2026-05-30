package com.cornea.management.service;

import com.cornea.management.dao.CornealExamDAO;
import com.cornea.management.entity.BiomechanicalParams;
import com.cornea.management.entity.CornealExam;
import com.cornea.management.entity.CornealTopography;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class CornealExamService {

    private final CornealExamDAO examDAO;
    private final PatientService patientService;

    public CornealExamService() {
        this.examDAO = new CornealExamDAO();
        this.patientService = new PatientService();
    }

    @Autowired
    public CornealExamService(CornealExamDAO examDAO, PatientService patientService) {
        this.examDAO = examDAO;
        this.patientService = patientService;
    }

    public CornealExam createExam(CornealExam exam) throws SQLException {
        if (exam.getPatientId() == null || !patientService.patientExists(exam.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + exam.getPatientId());
        }
        if (exam.getExamDate() == null) {
            throw new IllegalArgumentException("Exam date is required");
        }
        if (exam.getEyeSide() == null) {
            throw new IllegalArgumentException("Eye side is required");
        }
        if (exam.getExamType() == null) {
            throw new IllegalArgumentException("Exam type is required");
        }
        int examId = examDAO.insert(exam);
        return examDAO.findById(examId).orElseThrow();
    }

    public CornealExam updateExam(CornealExam exam) throws SQLException {
        examDAO.update(exam);
        if (exam.getTopography() != null) {
            examDAO.upsertTopography(exam.getTopography());
        }
        if (exam.getBiomechanics() != null) {
            examDAO.upsertBiomechanics(exam.getBiomechanics());
        }
        return examDAO.findById(exam.getExamId()).orElseThrow();
    }

    public void deleteExam(int examId) throws SQLException {
        examDAO.delete(examId);
    }

    public Optional<CornealExam> getExamById(int examId) throws SQLException {
        return examDAO.findById(examId);
    }

    public List<CornealExam> getExamsByPatientId(String patientId) throws SQLException {
        return examDAO.findByPatientId(patientId);
    }

    public void saveTopography(int examId, CornealTopography topo) throws SQLException {
        topo.setExamId(examId);
        if (examDAO.findTopographyByExamId(examId).isPresent()) {
            examDAO.updateTopography(topo);
        } else {
            throw new IllegalStateException(
                    "Topography must be created together with the exam. Use createExam() instead.");
        }
    }

    public void saveBiomechanics(int examId, BiomechanicalParams bio) throws SQLException {
        bio.setExamId(examId);
        if (examDAO.findBiomechanicsByExamId(examId).isPresent()) {
            examDAO.updateBiomechanics(bio);
        } else {
            throw new IllegalStateException(
                    "Biomechanics must be created together with the exam. Use createExam() instead.");
        }
    }
}
