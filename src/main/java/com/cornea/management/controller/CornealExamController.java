package com.cornea.management.controller;

import com.cornea.management.dto.ApiResponse;
import com.cornea.management.entity.CornealExam;
import com.cornea.management.service.CornealExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exams")
public class CornealExamController {

    private final CornealExamService examService;

    @Autowired
    public CornealExamController(CornealExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public ApiResponse<CornealExam> create(@RequestBody CornealExam exam) throws SQLException {
        return ApiResponse.success("Exam created", examService.createExam(exam));
    }

    @GetMapping("/{id}")
    public ApiResponse<CornealExam> getById(@PathVariable int id) throws SQLException {
        Optional<CornealExam> exam = examService.getExamById(id);
        if (exam.isEmpty()) {
            throw new IllegalArgumentException("Exam not found: " + id);
        }
        return ApiResponse.success(exam.get());
    }

    @GetMapping
    public ApiResponse<List<CornealExam>> listByPatient(@RequestParam String patientId) throws SQLException {
        return ApiResponse.success(examService.getExamsByPatientId(patientId));
    }

    @PutMapping("/{id}")
    public ApiResponse<CornealExam> update(@PathVariable int id, @RequestBody CornealExam exam) throws SQLException {
        exam.setExamId(id);
        return ApiResponse.success("Exam updated", examService.updateExam(exam));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) throws SQLException {
        examService.deleteExam(id);
        return ApiResponse.success("Exam deleted", null);
    }
}
