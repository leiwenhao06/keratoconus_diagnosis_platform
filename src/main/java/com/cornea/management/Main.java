package com.cornea.management;

import com.cornea.management.config.DatabaseConfig;
import com.cornea.management.entity.*;
import com.cornea.management.service.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 圆锥角膜患者信息管理系统 - 主入口
 * 数据库配置见 src/main/resources/application.properties
 */
public class Main {

    private static final PatientService patientService = new PatientService();
    private static final CornealExamService examService = new CornealExamService();
    private static final MedicalImageService imageService = new MedicalImageService();
    private static final MedicalRecordService recordService = new MedicalRecordService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("  圆锥角膜患者信息管理系统 v1.0");
        System.out.println("  Keratoconus Patient Management System");
        System.out.println("============================================");

        try {
            runDemo();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.close();
        }
    }

    private static void runDemo() throws SQLException {
        // ========================================
        // 1. 患者基本信息管理
        // ========================================
        System.out.println("\n--- 1. 患者基本信息管理 ---");

        Patient patient = new Patient("P20240501", "张三");
        patient.setGender("Male");
        patient.setDateOfBirth(LocalDate.of(1995, 6, 15));
        patient.setAge(29);
        patient.setIdCard("35010219950615001X");
        patient.setContact("13800138000");
        patient.setAddress("福建省福州市鼓楼区");
        patient.setMedicalHistory("无过敏史，否认高血压、糖尿病等慢性病史");

        if (!patientService.patientExists(patient.getPatientId())) {
            patientService.registerPatient(patient);
            System.out.println("  [+] 患者注册成功: " + patient);
        } else {
            System.out.println("  [i] 患者已存在: " + patient.getPatientId());
        }

        // 查询患者
        Optional<Patient> found = patientService.getPatientById("P20240501");
        found.ifPresent(p -> System.out.println("  [+] 查询患者: " + p));

        // 搜索患者
        List<Patient> searchResults = patientService.searchPatientsByName("张");
        System.out.println("  [+] 搜索'张': 找到 " + searchResults.size() + " 条记录");

        // ========================================
        // 2. 角膜生物力学检测 — 角膜地形图 + 生物力学参数
        // ========================================
        System.out.println("\n--- 2. 角膜生物力学检测 ---");

        CornealExam exam = new CornealExam();
        exam.setPatientId("P20240501");
        exam.setExamDate(LocalDate.now());
        exam.setEyeSide("right");
        exam.setExamType("Pentacam");
        exam.setDiagnosis("疑似圆锥角膜");

        // 角膜地形图参数 (Pentacam)
        CornealTopography topo = new CornealTopography();
        topo.setFrontK1(new BigDecimal("45.23"));
        topo.setFrontK2(new BigDecimal("47.56"));
        topo.setFrontKm(new BigDecimal("46.39"));
        topo.setFrontRf(new BigDecimal("7.45"));
        topo.setFrontRs(new BigDecimal("7.10"));
        topo.setFrontRm(new BigDecimal("7.27"));
        topo.setFrontQVal(new BigDecimal("-0.25"));
        topo.setFrontAstig(new BigDecimal("2.33"));
        topo.setFrontAxis(new BigDecimal("85.0"));

        topo.setBackK1(new BigDecimal("-6.12"));
        topo.setBackK2(new BigDecimal("-6.89"));
        topo.setBackKm(new BigDecimal("-6.50"));
        topo.setBackRf(new BigDecimal("6.38"));
        topo.setBackRs(new BigDecimal("6.01"));
        topo.setBackRm(new BigDecimal("6.19"));

        topo.setThinnestLocatPachyX(new BigDecimal("485.0"));
        topo.setThinnestLocatPachyY(new BigDecimal("-2.1"));
        topo.setCorneaVolume(new BigDecimal("61.2"));
        topo.setAcDepth(new BigDecimal("3.12"));
        topo.setIop(new BigDecimal("15.5"));
        topo.setPupilDia(new BigDecimal("4.5"));

        exam.setTopography(topo);

        // 生物力学参数 (Corvis ST)
        BiomechanicalParams bio = new BiomechanicalParams();
        bio.setCcbi(new BigDecimal("0.8523"));
        bio.setCtbi(new BigDecimal("0.7810"));
        bio.setIsValue(new BigDecimal("1.2500"));
        bio.setSpA1(new BigDecimal("89.5"));
        bio.setIntegrRadius(new BigDecimal("7.85"));
        bio.setArth(new BigDecimal("385.2"));
        bio.setDaRatio(new BigDecimal("1.62"));
        bio.setSsi(new BigDecimal("0.95"));

        exam.setBiomechanics(bio);

        CornealExam savedExam = examService.createExam(exam);
        System.out.println("  [+] 检查记录创建成功: exam_id=" + savedExam.getExamId());

        // 查询患者的全部检查
        List<CornealExam> exams = examService.getExamsByPatientId("P20240501");
        System.out.println("  [+] 患者检查记录数: " + exams.size());

        // ========================================
        // 3. 医学影像管理
        // ========================================
        System.out.println("\n--- 3. 医学影像管理 ---");

        MedicalImage image = new MedicalImage();
        image.setPatientId("P20240501");
        image.setExamId(savedExam.getExamId());
        image.setImageType("Pentacam");
        image.setEyeSide("right");
        image.setImagePath("/data/images/p20240501/pentacam_right_20240501.jpg");
        image.setFileName("pentacam_right_20240501.jpg");
        image.setFileSize(2048000L);

        MedicalImage savedImage = imageService.uploadImage(image);
        System.out.println("  [+] 影像上传成功: image_id=" + savedImage.getImageId());

        List<MedicalImage> images = imageService.getImagesByPatientId("P20240501");
        System.out.println("  [+] 患者影像数: " + images.size());

        // ========================================
        // 4. 病历管理
        // ========================================
        System.out.println("\n--- 4. 病历管理 ---");

        MedicalRecord record = new MedicalRecord();
        record.setPatientId("P20240501");
        record.setVisitDate(LocalDate.now());
        record.setChiefComplaint("右眼视力下降3个月，偶有视物重影");
        record.setPresentIllness("患者自述3月前无明显诱因出现右眼视力下降，伴轻度视物变形，无眼痛、眼红、畏光等不适");
        record.setPastHistory("无高血压、糖尿病史，无眼部外伤史，无角膜接触镜配戴史");
        record.setPhysicalExam("右眼裸眼视力0.4，矫正视力0.6；裂隙灯检查见角膜中央轻度隆起变薄");
        record.setDiagnosis("右眼圆锥角膜（早期/顿挫型）");
        record.setLeftEyeDiagnosis("正常");
        record.setRightEyeDiagnosis("顿挫型圆锥角膜 (Forme Fruste KC)");
        record.setTreatmentPlan("1. 右眼角膜胶原交联术(CXL)评估\n2. 定期随访角膜地形图每6个月\n3. 左眼定期观察");
        record.setDoctorNotes("建议完善Corvis ST生物力学检查及前节OCT，综合评估后确定手术时机");
        record.setDoctorName("陈主任");

        MedicalRecord savedRecord = recordService.createRecord(record);
        System.out.println("  [+] 病历创建成功: record_id=" + savedRecord.getRecordId());

        List<MedicalRecord> records = recordService.getRecordsByPatientId("P20240501");
        System.out.println("  [+] 患者病历数: " + records.size());

        // ========================================
        // 5. 综合查询展示
        // ========================================
        System.out.println("\n--- 5. 综合查询展示 ---");
        System.out.println("============================================");
        printPatientSummary("P20240501");

        System.out.println("\n============================================");
        System.out.println("  系统演示完成。所有功能正常运行。");
        System.out.println("============================================");
    }

    private static void printPatientSummary(String patientId) throws SQLException {
        Optional<Patient> patient = patientService.getPatientById(patientId);
        if (patient.isEmpty()) {
            System.out.println("患者不存在: " + patientId);
            return;
        }
        Patient p = patient.get();
        System.out.println("患者ID: " + p.getPatientId());
        System.out.println("姓名: " + p.getName());
        System.out.println("性别: " + p.getGender() + " | 出生日期: " + p.getDateOfBirth() + " | 年龄: " + p.getAge());

        List<CornealExam> exams = examService.getExamsByPatientId(patientId);
        System.out.println("角膜检查记录数: " + exams.size());
        for (CornealExam exam : exams) {
            System.out.println("  - [" + exam.getExamId() + "] " + exam.getExamDate()
                    + " | " + exam.getEyeSide() + " | " + exam.getExamType()
                    + " | 诊断: " + exam.getDiagnosis());
            if (exam.getTopography() != null) {
                CornealTopography t = exam.getTopography();
                System.out.println("    Km: " + t.getFrontKm() + "D | 最薄点厚度: "
                        + t.getThinnestLocatPachyX() + "μm | IOP: " + t.getIop() + "mmHg");
            }
            if (exam.getBiomechanics() != null) {
                BiomechanicalParams b = exam.getBiomechanics();
                System.out.println("    cCBI: " + b.getCcbi() + " | cTBI: " + b.getCtbi()
                        + " | SSI: " + b.getSsi() + " | SP-A1: " + b.getSpA1());
            }
        }

        List<MedicalRecord> records = recordService.getRecordsByPatientId(patientId);
        System.out.println("病历记录数: " + records.size());
        for (MedicalRecord r : records) {
            System.out.println("  - [" + r.getRecordId() + "] " + r.getVisitDate()
                    + " | 医生: " + r.getDoctorName() + " | 诊断: " + r.getDiagnosis());
        }

        List<MedicalImage> images = imageService.getImagesByPatientId(patientId);
        System.out.println("医学影像数: " + images.size());
        for (MedicalImage img : images) {
            System.out.println("  - [" + img.getImageId() + "] " + img.getImageType()
                    + " | " + img.getEyeSide() + " | " + img.getFileName());
        }
    }
}
