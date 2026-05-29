-- ============================================================
-- 圆锥角膜患者信息管理系统 - 数据库初始化脚本
-- Keratoconus Patient Information Management System
-- ============================================================

CREATE DATABASE IF NOT EXISTS cornea_patient_management
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE cornea_patient_management;

-- ============================================================
-- 1. 患者基本信息表
-- ============================================================
CREATE TABLE IF NOT EXISTS patients (
    patient_id      VARCHAR(32)     PRIMARY KEY     COMMENT '患者唯一ID',
    name            VARCHAR(100)    NOT NULL        COMMENT '姓名',
    gender          ENUM('Male','Female','Other')   COMMENT '性别',
    date_of_birth   DATE                            COMMENT '出生日期',
    age             INT                             COMMENT '年龄',
    id_card         VARCHAR(18)                     COMMENT '身份证号',
    contact         VARCHAR(20)                     COMMENT '联系方式',
    address         VARCHAR(255)                    COMMENT '地址',
    medical_history TEXT                            COMMENT '既往病史',
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_gender (gender),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者基本信息表';

-- ============================================================
-- 2. 角膜检查记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS corneal_exams (
    exam_id         INT             AUTO_INCREMENT PRIMARY KEY,
    patient_id      VARCHAR(32)     NOT NULL        COMMENT '患者ID',
    exam_date       DATE            NOT NULL        COMMENT '检查日期',
    eye_side        ENUM('left','right','both') NOT NULL COMMENT '眼别',
    exam_type       ENUM('Pentacam','Corvis_TBI','Corvis_VSR','Other') NOT NULL COMMENT '检查类型',
    diagnosis       VARCHAR(100)                    COMMENT '诊断结果',
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    INDEX idx_patient_exam (patient_id, exam_date),
    INDEX idx_exam_date (exam_date),
    INDEX idx_exam_type (exam_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角膜检查记录表';

-- ============================================================
-- 3. 角膜地形图参数表 (Pentacam 前/后表面 + 厚度 + 其他)
-- ============================================================
CREATE TABLE IF NOT EXISTS corneal_topography (
    id                  INT             AUTO_INCREMENT PRIMARY KEY,
    exam_id             INT             NOT NULL        COMMENT '关联检查ID',
    -- 前表面参数
    front_rf            DECIMAL(8,3)                    COMMENT '前表面平坦曲率半径 (mm)',
    front_rs            DECIMAL(8,3)                    COMMENT '前表面陡峭曲率半径 (mm)',
    front_rm            DECIMAL(8,3)                    COMMENT '前表面平均曲率半径 (mm)',
    front_k1            DECIMAL(8,3)                    COMMENT '前表面K1 (D)',
    front_k2            DECIMAL(8,3)                    COMMENT '前表面K2 (D)',
    front_km            DECIMAL(8,3)                    COMMENT '前表面平均K (D)',
    front_q_val         DECIMAL(8,4)                    COMMENT '前表面非球面系数Q',
    front_rper          DECIMAL(8,3)                    COMMENT '前表面Rper (mm)',
    front_rmin          DECIMAL(8,3)                    COMMENT '前表面Rmin (mm)',
    front_axis          DECIMAL(8,3)                    COMMENT '前表面轴位 (°)',
    front_astig         DECIMAL(8,3)                    COMMENT '前表面散光 (D)',
    -- 后表面参数
    back_rf             DECIMAL(8,3)                    COMMENT '后表面平坦曲率半径 (mm)',
    back_rs             DECIMAL(8,3)                    COMMENT '后表面陡峭曲率半径 (mm)',
    back_rm             DECIMAL(8,3)                    COMMENT '后表面平均曲率半径 (mm)',
    back_k1             DECIMAL(8,3)                    COMMENT '后表面K1 (D)',
    back_k2             DECIMAL(8,3)                    COMMENT '后表面K2 (D)',
    back_km             DECIMAL(8,3)                    COMMENT '后表面平均K (D)',
    back_q_val          DECIMAL(8,4)                    COMMENT '后表面非球面系数Q',
    back_rper           DECIMAL(8,3)                    COMMENT '后表面Rper (mm)',
    back_rmin           DECIMAL(8,3)                    COMMENT '后表面Rmin (mm)',
    back_axis           DECIMAL(8,3)                    COMMENT '后表面轴位 (°)',
    back_astig          DECIMAL(8,3)                    COMMENT '后表面散光 (D)',
    -- 角膜厚度参数
    pupil_center_pachy_x DECIMAL(8,3)                   COMMENT '瞳孔中心厚度X坐标 (mm)',
    pupil_center_pachy_y DECIMAL(8,3)                   COMMENT '瞳孔中心厚度Y坐标 (mm)',
    pachy_apex_x        DECIMAL(8,3)                    COMMENT '顶点厚度X坐标 (mm)',
    pachy_apex_y        DECIMAL(8,3)                    COMMENT '顶点厚度Y坐标 (mm)',
    thinnest_locat_pachy_x DECIMAL(8,3)                 COMMENT '最薄点厚度X坐标 (mm)',
    thinnest_locat_pachy_y DECIMAL(8,3)                 COMMENT '最薄点厚度Y坐标 (mm)',
    k_max_pachy_x       DECIMAL(8,3)                    COMMENT '最大K值点厚度X坐标 (mm)',
    k_max_pachy_y       DECIMAL(8,3)                    COMMENT '最大K值点厚度Y坐标 (mm)',
    -- 其他眼前节参数
    cornea_volume       DECIMAL(8,3)                    COMMENT '角膜容积 (mm³)',
    chamber_volume      DECIMAL(8,3)                    COMMENT '前房容积 (mm³)',
    angle               DECIMAL(8,3)                    COMMENT '房角 (°)',
    ac_depth            DECIMAL(8,3)                    COMMENT '前房深度 (mm)',
    pupil_dia           DECIMAL(8,3)                    COMMENT '瞳孔直径 (mm)',
    iop                 DECIMAL(8,3)                    COMMENT '眼压 (mmHg)',
    lens_th             DECIMAL(8,3)                    COMMENT '晶状体厚度 (mm)',
    FOREIGN KEY (exam_id) REFERENCES corneal_exams(exam_id) ON DELETE CASCADE,
    UNIQUE KEY uk_exam (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角膜地形图参数表';

-- ============================================================
-- 4. 角膜生物力学参数表 (Corvis ST)
-- ============================================================
CREATE TABLE IF NOT EXISTS biomechanical_params (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    exam_id         INT             NOT NULL            COMMENT '关联检查ID',
    ccbi            DECIMAL(8,4)                        COMMENT '角膜生物力学指数 cCBI',
    ctbi            DECIMAL(8,4)                        COMMENT '角膜层析生物力学指数 cTBI',
    is_value        DECIMAL(8,4)                        COMMENT '逆向凹面半径 IS-Value',
    sp_a1           DECIMAL(8,4)                        COMMENT '刚度参数 SP-A1',
    integr_radius   DECIMAL(8,4)                        COMMENT '综合半径 (mm)',
    arth            DECIMAL(8,4)                        COMMENT 'Ambrósio相对厚度 ARTh',
    da_ratio        DECIMAL(8,4)                        COMMENT '形变幅度比 DA Ratio',
    ssi             DECIMAL(8,4)                        COMMENT '应力-应变指数 SSI',
    FOREIGN KEY (exam_id) REFERENCES corneal_exams(exam_id) ON DELETE CASCADE,
    UNIQUE KEY uk_exam (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角膜生物力学参数表';

-- ============================================================
-- 5. 医学影像图片表
-- ============================================================
CREATE TABLE IF NOT EXISTS medical_images (
    image_id        INT             AUTO_INCREMENT PRIMARY KEY,
    patient_id      VARCHAR(32)     NOT NULL            COMMENT '患者ID',
    exam_id         INT                                 COMMENT '关联检查ID',
    image_type      ENUM('Pentacam','Corvis_TBI','Corvis_VSR','Fundus','Slit_Lamp','OCT','Other') NOT NULL COMMENT '影像类型',
    eye_side        ENUM('left','right','both')         COMMENT '眼别',
    image_path      VARCHAR(500)                        COMMENT '文件路径',
    image_data      LONGBLOB                            COMMENT '影像二进制数据',
    file_name       VARCHAR(255)                        COMMENT '文件名',
    file_size       BIGINT                              COMMENT '文件大小 (bytes)',
    upload_date     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES corneal_exams(exam_id) ON DELETE SET NULL,
    INDEX idx_patient_image (patient_id, upload_date),
    INDEX idx_image_type (image_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医学影像图片表';

-- ============================================================
-- 6. 病历记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS medical_records (
    record_id           INT             AUTO_INCREMENT PRIMARY KEY,
    patient_id          VARCHAR(32)     NOT NULL        COMMENT '患者ID',
    visit_date          DATE            NOT NULL        COMMENT '就诊日期',
    chief_complaint     TEXT                            COMMENT '主诉',
    present_illness     TEXT                            COMMENT '现病史',
    past_history        TEXT                            COMMENT '既往史',
    physical_exam       TEXT                            COMMENT '体格检查',
    diagnosis           TEXT                            COMMENT '诊断意见',
    left_eye_diagnosis  VARCHAR(255)                    COMMENT '左眼诊断',
    right_eye_diagnosis VARCHAR(255)                    COMMENT '右眼诊断',
    treatment_plan      TEXT                            COMMENT '治疗方案',
    doctor_notes        TEXT                            COMMENT '医嘱/备注',
    doctor_name         VARCHAR(100)                    COMMENT '医生姓名',
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    INDEX idx_patient_record (patient_id, visit_date),
    INDEX idx_visit_date (visit_date),
    INDEX idx_doctor (doctor_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='病历记录表';
