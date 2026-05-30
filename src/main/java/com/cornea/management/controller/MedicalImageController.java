package com.cornea.management.controller;

import com.cornea.management.dto.ApiResponse;
import com.cornea.management.entity.MedicalImage;
import com.cornea.management.service.MedicalImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class MedicalImageController {

    private final MedicalImageService imageService;

    public MedicalImageController() {
        this.imageService = new MedicalImageService();
    }

    @Autowired
    public MedicalImageController(MedicalImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ApiResponse<MedicalImage> upload(@RequestBody MedicalImage image) throws SQLException {
        return ApiResponse.success("Image uploaded", imageService.uploadImage(image));
    }

    @GetMapping
    public ApiResponse<List<MedicalImage>> list(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) Integer examId) throws SQLException {
        if (examId != null) {
            return ApiResponse.success(imageService.getImagesByExamId(examId));
        }
        if (patientId != null && !patientId.isBlank()) {
            return ApiResponse.success(imageService.getImagesByPatientId(patientId));
        }
        throw new IllegalArgumentException("Either patientId or examId is required");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) throws SQLException {
        imageService.deleteImage(id);
        return ApiResponse.success("Image deleted", null);
    }
}
