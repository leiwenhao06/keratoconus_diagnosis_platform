package com.cornea.management.controller;

import com.cornea.management.dto.ApiResponse;
import com.cornea.management.entity.MedicalRecord;
import com.cornea.management.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/records")
public class MedicalRecordController {

    private final MedicalRecordService recordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ApiResponse<MedicalRecord> create(@RequestBody MedicalRecord record) throws SQLException {
        return ApiResponse.success("Record created", recordService.createRecord(record));
    }

    @GetMapping("/{id}")
    public ApiResponse<MedicalRecord> getById(@PathVariable int id) throws SQLException {
        Optional<MedicalRecord> record = recordService.getRecordById(id);
        if (record.isEmpty()) {
            throw new IllegalArgumentException("Record not found: " + id);
        }
        return ApiResponse.success(record.get());
    }

    @GetMapping
    public ApiResponse<List<MedicalRecord>> listByPatient(@RequestParam String patientId) throws SQLException {
        return ApiResponse.success(recordService.getRecordsByPatientId(patientId));
    }

    @PutMapping("/{id}")
    public ApiResponse<MedicalRecord> update(@PathVariable int id, @RequestBody MedicalRecord record) throws SQLException {
        record.setRecordId(id);
        return ApiResponse.success("Record updated", recordService.updateRecord(record));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) throws SQLException {
        recordService.deleteRecord(id);
        return ApiResponse.success("Record deleted", null);
    }
}
