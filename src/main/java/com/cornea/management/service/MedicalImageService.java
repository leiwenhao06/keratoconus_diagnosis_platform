package com.cornea.management.service;

import com.cornea.management.dao.MedicalImageDAO;
import com.cornea.management.entity.MedicalImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MedicalImageService {

    private static final Logger log = LoggerFactory.getLogger(MedicalImageService.class);

    private static final java.util.Set<String> ALLOWED_EXTENSIONS = java.util.Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".tiff", ".tif"
    );

    private final MedicalImageDAO imageDAO;
    private final PatientService patientService;

    @Value("${file.upload.path:./data/images/}")
    private String uploadPath;

    @Autowired
    public MedicalImageService(MedicalImageDAO imageDAO, PatientService patientService) {
        this.imageDAO = imageDAO;
        this.patientService = patientService;
    }

    /**
     * 通过 MultipartFile 上传图片，保存到专用文件夹，数据库存储 UUID 编号
     */
    @Transactional(rollbackFor = {SQLException.class, IOException.class})
    public MedicalImage uploadImageFile(MultipartFile file, String patientId,
                                         String imageType, String eyeSide, Integer examId)
            throws SQLException, IOException {
        if (!patientService.patientExists(patientId)) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }
        if (imageType == null) {
            throw new IllegalArgumentException("Image type is required");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Upload file is empty");
        }

        // 确保上传目录存在
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 用 UUID 生成唯一文件名，保留原始扩展名
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                "不支持的文件格式：" + extension + "。允许的格式：jpg, jpeg, png, gif, bmp, webp, tiff"
            );
        }
        String uuidFileName = UUID.randomUUID().toString() + extension;

        // 写入磁盘
        Path dest = uploadDir.resolve(uuidFileName);
        file.transferTo(dest.toFile());
        log.info("Image file saved to disk: {} (original: {}, size: {} bytes)",
                uuidFileName, originalName, file.getSize());

        // 数据库记录：image_path 存储 UUID 文件名
        MedicalImage image = new MedicalImage();
        image.setPatientId(patientId);
        image.setExamId(examId);
        image.setImageType(imageType);
        image.setEyeSide(eyeSide);
        image.setImagePath(uuidFileName);
        image.setFileName(originalName);
        image.setFileSize(file.getSize());

        int id = imageDAO.insert(image);
        log.info("Image record inserted: imageId={}, uuid={}, patientId={}", id, uuidFileName, patientId);
        return imageDAO.findById(id)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve uploaded image: " + id));
    }

    /**
     * 根据 UUID 编号从磁盘读取图片文件
     */
    public byte[] getImageFileByUuid(String uuid) throws SQLException, IOException {
        Optional<MedicalImage> imageOpt = imageDAO.findByImagePath(uuid);
        if (imageOpt.isEmpty()) {
            throw new IllegalArgumentException("Image not found: " + uuid);
        }
        Path filePath = Paths.get(uploadPath, uuid);
        if (!Files.exists(filePath)) {
            log.error("Image file missing on disk: {}", uuid);
            throw new IllegalArgumentException("Image file not found on disk: " + uuid);
        }
        return Files.readAllBytes(filePath);
    }

    /**
     * 根据 UUID 获取图片元数据
     */
    public Optional<MedicalImage> getImageInfoByUuid(String uuid) throws SQLException {
        return imageDAO.findByImagePath(uuid);
    }

    // ==================== 原有方法 ====================

    @Transactional(rollbackFor = SQLException.class)
    public MedicalImage uploadImage(MedicalImage image) throws SQLException {
        if (!patientService.patientExists(image.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + image.getPatientId());
        }
        if (image.getImageType() == null) {
            throw new IllegalArgumentException("Image type is required");
        }
        int id = imageDAO.insert(image);
        log.info("Image uploaded (legacy): imageId={}, patientId={}", id, image.getPatientId());
        return imageDAO.findById(id)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve uploaded image: " + id));
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

    @Transactional(rollbackFor = SQLException.class)
    public void deleteImage(int imageId) throws SQLException {
        imageDAO.delete(imageId);
        log.info("Image deleted: imageId={}", imageId);
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
