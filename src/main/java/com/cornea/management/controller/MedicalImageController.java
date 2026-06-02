package com.cornea.management.controller;

import com.cornea.management.dto.ApiResponse;
import com.cornea.management.entity.MedicalImage;
import com.cornea.management.service.MedicalImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MedicalImageController {

    private static final Logger log = LoggerFactory.getLogger(MedicalImageController.class);

    private final MedicalImageService imageService;

    @Autowired
    public MedicalImageController(MedicalImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 文件上传接口：接收 MultipartFile，保存到磁盘，返回 UUID 编号
     */
    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("patientId") String patientId,
            @RequestParam("imageType") String imageType,
            @RequestParam(value = "eyeSide", required = false) String eyeSide,
            @RequestParam(value = "examId", required = false) Integer examId)
            throws SQLException, IOException {
        log.info("Image upload request: patientId={}, imageType={}, eyeSide={}, size={}",
                patientId, imageType, eyeSide, file.getSize());
        MedicalImage image = imageService.uploadImageFile(file, patientId, imageType, eyeSide, examId);
        Map<String, String> result = new HashMap<>();
        result.put("uuid", image.getImagePath());
        result.put("imageId", String.valueOf(image.getImageId()));
        result.put("fileName", image.getFileName() != null ? image.getFileName() : "unknown");
        log.info("Image upload success: uuid={}, imageId={}", image.getImagePath(), image.getImageId());
        return ApiResponse.success("文件上传成功", result);
    }

    /**
     * 图片访问接口：根据 UUID 编号返回图片文件流
     */
    @GetMapping("/images/view/{uuid}")
    public void viewImage(@PathVariable String uuid, HttpServletResponse response)
            throws SQLException, IOException {
        MedicalImage image = imageService.getImageInfoByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + uuid));
        byte[] data = imageService.getImageFileByUuid(uuid);

        String contentType = guessContentType(uuid);
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=86400");
        String safeFileName = image.getFileName() != null ? image.getFileName() : uuid;
        response.setHeader("Content-Disposition", "inline; filename=\"" + safeFileName + "\"");
        try (OutputStream os = response.getOutputStream()) {
            os.write(data);
            os.flush();
        }
    }

    private String guessContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }

    // ==================== 原有元数据 CRUD ====================

    @PostMapping("/images")
    public ApiResponse<MedicalImage> upload(@RequestBody MedicalImage image) throws SQLException {
        return ApiResponse.success("Image uploaded", imageService.uploadImage(image));
    }

    @GetMapping("/images")
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

    @DeleteMapping("/images/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) throws SQLException {
        imageService.deleteImage(id);
        return ApiResponse.success("Image deleted", null);
    }
}
