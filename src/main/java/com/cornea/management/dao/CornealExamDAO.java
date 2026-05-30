package com.cornea.management.dao;

import com.cornea.management.config.DatabaseConfig;
import com.cornea.management.entity.BiomechanicalParams;
import com.cornea.management.entity.CornealExam;
import com.cornea.management.entity.CornealTopography;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CornealExamDAO {

    // ==================== Exam CRUD ====================

    public int insert(CornealExam exam) throws SQLException {
        String sql = "INSERT INTO corneal_exams (patient_id, exam_date, eye_side, exam_type, diagnosis) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int examId;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, exam.getPatientId());
                    ps.setDate(2, Date.valueOf(exam.getExamDate()));
                    ps.setString(3, exam.getEyeSide());
                    ps.setString(4, exam.getExamType());
                    ps.setString(5, exam.getDiagnosis());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Failed to insert corneal exam");
                        examId = rs.getInt(1);
                    }
                }
                exam.setExamId(examId);

                if (exam.getTopography() != null) {
                    exam.getTopography().setExamId(examId);
                    insertTopography(conn, exam.getTopography());
                }
                if (exam.getBiomechanics() != null) {
                    exam.getBiomechanics().setExamId(examId);
                    insertBiomechanics(conn, exam.getBiomechanics());
                }

                conn.commit();
                return examId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void update(CornealExam exam) throws SQLException {
        String sql = "UPDATE corneal_exams SET exam_date=?, eye_side=?, exam_type=?, diagnosis=? WHERE exam_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(exam.getExamDate()));
            ps.setString(2, exam.getEyeSide());
            ps.setString(3, exam.getExamType());
            ps.setString(4, exam.getDiagnosis());
            ps.setInt(5, exam.getExamId());
            ps.executeUpdate();
        }
    }

    public void delete(int examId) throws SQLException {
        String sql = "DELETE FROM corneal_exams WHERE exam_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.executeUpdate();
        }
    }

    public Optional<CornealExam> findById(int examId) throws SQLException {
        String sql = "SELECT * FROM corneal_exams WHERE exam_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                CornealExam exam = mapExamRow(rs);
                exam.setTopography(findTopographyByExamId(examId).orElse(null));
                exam.setBiomechanics(findBiomechanicsByExamId(examId).orElse(null));
                return Optional.of(exam);
            }
        }
    }

    public List<CornealExam> findByPatientId(String patientId) throws SQLException {
        String sql = "SELECT * FROM corneal_exams WHERE patient_id=? ORDER BY exam_date DESC";
        List<CornealExam> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapExamRow(rs));
                }
            }
        }
        if (!list.isEmpty()) {
            List<Integer> examIds = list.stream().map(CornealExam::getExamId).collect(Collectors.toList());
            Map<Integer, CornealTopography> topoMap = findTopographyByExamIds(examIds);
            Map<Integer, BiomechanicalParams> bioMap = findBiomechanicsByExamIds(examIds);
            for (CornealExam exam : list) {
                exam.setTopography(topoMap.get(exam.getExamId()));
                exam.setBiomechanics(bioMap.get(exam.getExamId()));
            }
        }
        return list;
    }

    // ==================== Topography ====================

    private void insertTopography(Connection conn, CornealTopography topo) throws SQLException {
        String sql = "INSERT INTO corneal_topography (exam_id, front_rf, front_rs, front_rm, " +
                "front_k1, front_k2, front_km, front_q_val, front_rper, front_rmin, front_axis, front_astig, " +
                "back_rf, back_rs, back_rm, back_k1, back_k2, back_km, back_q_val, back_rper, back_rmin, back_axis, back_astig, " +
                "pupil_center_pachy_x, pupil_center_pachy_y, pachy_apex_x, pachy_apex_y, " +
                "thinnest_locat_pachy_x, thinnest_locat_pachy_y, k_max_pachy_x, k_max_pachy_y, " +
                "cornea_volume, chamber_volume, angle, ac_depth, pupil_dia, iop, lens_th) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setTopographyParams(ps, topo);
            ps.executeUpdate();
        }
    }

    public void updateTopography(CornealTopography topo) throws SQLException {
        String sql = "UPDATE corneal_topography SET front_rf=?, front_rs=?, front_rm=?, " +
                "front_k1=?, front_k2=?, front_km=?, front_q_val=?, front_rper=?, front_rmin=?, front_axis=?, front_astig=?, " +
                "back_rf=?, back_rs=?, back_rm=?, back_k1=?, back_k2=?, back_km=?, back_q_val=?, back_rper=?, back_rmin=?, back_axis=?, back_astig=?, " +
                "pupil_center_pachy_x=?, pupil_center_pachy_y=?, pachy_apex_x=?, pachy_apex_y=?, " +
                "thinnest_locat_pachy_x=?, thinnest_locat_pachy_y=?, k_max_pachy_x=?, k_max_pachy_y=?, " +
                "cornea_volume=?, chamber_volume=?, angle=?, ac_depth=?, pupil_dia=?, iop=?, lens_th=? WHERE exam_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setTopographyParams(ps, topo);
            ps.setInt(38, topo.getExamId());
            ps.executeUpdate();
        }
    }

    public void upsertTopography(CornealTopography topo) throws SQLException {
        if (findTopographyByExamId(topo.getExamId()).isPresent()) {
            updateTopography(topo);
        } else {
            String sql = "INSERT INTO corneal_topography (exam_id, front_rf, front_rs, front_rm, " +
                    "front_k1, front_k2, front_km, front_q_val, front_rper, front_rmin, front_axis, front_astig, " +
                    "back_rf, back_rs, back_rm, back_k1, back_k2, back_km, back_q_val, back_rper, back_rmin, back_axis, back_astig, " +
                    "pupil_center_pachy_x, pupil_center_pachy_y, pachy_apex_x, pachy_apex_y, " +
                    "thinnest_locat_pachy_x, thinnest_locat_pachy_y, k_max_pachy_x, k_max_pachy_y, " +
                    "cornea_volume, chamber_volume, angle, ac_depth, pupil_dia, iop, lens_th) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                setTopographyParams(ps, topo);
                ps.executeUpdate();
            }
        }
    }

    public Optional<CornealTopography> findTopographyByExamId(int examId) throws SQLException {
        String sql = "SELECT * FROM corneal_topography WHERE exam_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapTopographyRow(rs));
            }
        }
    }

    private Map<Integer, CornealTopography> findTopographyByExamIds(List<Integer> examIds) throws SQLException {
        if (examIds.isEmpty()) return Collections.emptyMap();
        String placeholders = examIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT * FROM corneal_topography WHERE exam_id IN (" + placeholders + ")";
        Map<Integer, CornealTopography> map = new HashMap<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < examIds.size(); i++) {
                ps.setInt(i + 1, examIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CornealTopography topo = mapTopographyRow(rs);
                    map.put(topo.getExamId(), topo);
                }
            }
        }
        return map;
    }

    // ==================== Biomechanics ====================

    private void insertBiomechanics(Connection conn, BiomechanicalParams bio) throws SQLException {
        String sql = "INSERT INTO biomechanical_params (exam_id, ccbi, ctbi, is_value, sp_a1, " +
                "integr_radius, arth, da_ratio, ssi) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setBiomechanicsParams(ps, bio);
            ps.executeUpdate();
        }
    }

    public void updateBiomechanics(BiomechanicalParams bio) throws SQLException {
        String sql = "UPDATE biomechanical_params SET ccbi=?, ctbi=?, is_value=?, sp_a1=?, " +
                "integr_radius=?, arth=?, da_ratio=?, ssi=? WHERE exam_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setBiomechanicsParams(ps, bio);
            ps.setInt(9, bio.getExamId());
            ps.executeUpdate();
        }
    }

    public void upsertBiomechanics(BiomechanicalParams bio) throws SQLException {
        if (findBiomechanicsByExamId(bio.getExamId()).isPresent()) {
            updateBiomechanics(bio);
        } else {
            String sql = "INSERT INTO biomechanical_params (exam_id, ccbi, ctbi, is_value, sp_a1, " +
                    "integr_radius, arth, da_ratio, ssi) VALUES (?,?,?,?,?,?,?,?,?)";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                setBiomechanicsParams(ps, bio);
                ps.executeUpdate();
            }
        }
    }

    public Optional<BiomechanicalParams> findBiomechanicsByExamId(int examId) throws SQLException {
        String sql = "SELECT * FROM biomechanical_params WHERE exam_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapBiomechanicsRow(rs));
            }
        }
    }

    private Map<Integer, BiomechanicalParams> findBiomechanicsByExamIds(List<Integer> examIds) throws SQLException {
        if (examIds.isEmpty()) return Collections.emptyMap();
        String placeholders = examIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT * FROM biomechanical_params WHERE exam_id IN (" + placeholders + ")";
        Map<Integer, BiomechanicalParams> map = new HashMap<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < examIds.size(); i++) {
                ps.setInt(i + 1, examIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BiomechanicalParams bio = mapBiomechanicsRow(rs);
                    map.put(bio.getExamId(), bio);
                }
            }
        }
        return map;
    }

    // ==================== Helper ====================

    private void setTopographyParams(PreparedStatement ps, CornealTopography t) throws SQLException {
        int i = 1;
        ps.setInt(i++, t.getExamId());
        setDecimal(ps, i++, t.getFrontRf()); setDecimal(ps, i++, t.getFrontRs()); setDecimal(ps, i++, t.getFrontRm());
        setDecimal(ps, i++, t.getFrontK1()); setDecimal(ps, i++, t.getFrontK2()); setDecimal(ps, i++, t.getFrontKm());
        setDecimal(ps, i++, t.getFrontQVal()); setDecimal(ps, i++, t.getFrontRper()); setDecimal(ps, i++, t.getFrontRmin());
        setDecimal(ps, i++, t.getFrontAxis()); setDecimal(ps, i++, t.getFrontAstig());
        setDecimal(ps, i++, t.getBackRf()); setDecimal(ps, i++, t.getBackRs()); setDecimal(ps, i++, t.getBackRm());
        setDecimal(ps, i++, t.getBackK1()); setDecimal(ps, i++, t.getBackK2()); setDecimal(ps, i++, t.getBackKm());
        setDecimal(ps, i++, t.getBackQVal()); setDecimal(ps, i++, t.getBackRper()); setDecimal(ps, i++, t.getBackRmin());
        setDecimal(ps, i++, t.getBackAxis()); setDecimal(ps, i++, t.getBackAstig());
        setDecimal(ps, i++, t.getPupilCenterPachyX()); setDecimal(ps, i++, t.getPupilCenterPachyY());
        setDecimal(ps, i++, t.getPachyApexX()); setDecimal(ps, i++, t.getPachyApexY());
        setDecimal(ps, i++, t.getThinnestLocatPachyX()); setDecimal(ps, i++, t.getThinnestLocatPachyY());
        setDecimal(ps, i++, t.getKMaxPachyX()); setDecimal(ps, i++, t.getKMaxPachyY());
        setDecimal(ps, i++, t.getCorneaVolume()); setDecimal(ps, i++, t.getChamberVolume());
        setDecimal(ps, i++, t.getAngle()); setDecimal(ps, i++, t.getAcDepth());
        setDecimal(ps, i++, t.getPupilDia()); setDecimal(ps, i++, t.getIop()); setDecimal(ps, i++, t.getLensTh());
    }

    private void setBiomechanicsParams(PreparedStatement ps, BiomechanicalParams b) throws SQLException {
        int i = 1;
        ps.setInt(i++, b.getExamId());
        setDecimal(ps, i++, b.getCcbi()); setDecimal(ps, i++, b.getCtbi());
        setDecimal(ps, i++, b.getIsValue()); setDecimal(ps, i++, b.getSpA1());
        setDecimal(ps, i++, b.getIntegrRadius()); setDecimal(ps, i++, b.getArth());
        setDecimal(ps, i++, b.getDaRatio()); setDecimal(ps, i++, b.getSsi());
    }

    private void setDecimal(PreparedStatement ps, int index, BigDecimal value) throws SQLException {
        if (value != null) ps.setBigDecimal(index, value);
        else ps.setNull(index, Types.DECIMAL);
    }

    private CornealExam mapExamRow(ResultSet rs) throws SQLException {
        CornealExam exam = new CornealExam();
        exam.setExamId(rs.getInt("exam_id"));
        exam.setPatientId(rs.getString("patient_id"));
        exam.setExamDate(rs.getDate("exam_date").toLocalDate());
        exam.setEyeSide(rs.getString("eye_side"));
        exam.setExamType(rs.getString("exam_type"));
        exam.setDiagnosis(rs.getString("diagnosis"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) exam.setCreatedAt(ca.toLocalDateTime());
        return exam;
    }

    private CornealTopography mapTopographyRow(ResultSet rs) throws SQLException {
        CornealTopography t = new CornealTopography();
        t.setId(rs.getInt("id"));
        t.setExamId(rs.getInt("exam_id"));
        t.setFrontRf(rs.getBigDecimal("front_rf")); t.setFrontRs(rs.getBigDecimal("front_rs"));
        t.setFrontRm(rs.getBigDecimal("front_rm")); t.setFrontK1(rs.getBigDecimal("front_k1"));
        t.setFrontK2(rs.getBigDecimal("front_k2")); t.setFrontKm(rs.getBigDecimal("front_km"));
        t.setFrontQVal(rs.getBigDecimal("front_q_val")); t.setFrontRper(rs.getBigDecimal("front_rper"));
        t.setFrontRmin(rs.getBigDecimal("front_rmin")); t.setFrontAxis(rs.getBigDecimal("front_axis"));
        t.setFrontAstig(rs.getBigDecimal("front_astig"));
        t.setBackRf(rs.getBigDecimal("back_rf")); t.setBackRs(rs.getBigDecimal("back_rs"));
        t.setBackRm(rs.getBigDecimal("back_rm")); t.setBackK1(rs.getBigDecimal("back_k1"));
        t.setBackK2(rs.getBigDecimal("back_k2")); t.setBackKm(rs.getBigDecimal("back_km"));
        t.setBackQVal(rs.getBigDecimal("back_q_val")); t.setBackRper(rs.getBigDecimal("back_rper"));
        t.setBackRmin(rs.getBigDecimal("back_rmin")); t.setBackAxis(rs.getBigDecimal("back_axis"));
        t.setBackAstig(rs.getBigDecimal("back_astig"));
        t.setPupilCenterPachyX(rs.getBigDecimal("pupil_center_pachy_x"));
        t.setPupilCenterPachyY(rs.getBigDecimal("pupil_center_pachy_y"));
        t.setPachyApexX(rs.getBigDecimal("pachy_apex_x"));
        t.setPachyApexY(rs.getBigDecimal("pachy_apex_y"));
        t.setThinnestLocatPachyX(rs.getBigDecimal("thinnest_locat_pachy_x"));
        t.setThinnestLocatPachyY(rs.getBigDecimal("thinnest_locat_pachy_y"));
        t.setKMaxPachyX(rs.getBigDecimal("k_max_pachy_x"));
        t.setKMaxPachyY(rs.getBigDecimal("k_max_pachy_y"));
        t.setCorneaVolume(rs.getBigDecimal("cornea_volume")); t.setChamberVolume(rs.getBigDecimal("chamber_volume"));
        t.setAngle(rs.getBigDecimal("angle")); t.setAcDepth(rs.getBigDecimal("ac_depth"));
        t.setPupilDia(rs.getBigDecimal("pupil_dia")); t.setIop(rs.getBigDecimal("iop"));
        t.setLensTh(rs.getBigDecimal("lens_th"));
        return t;
    }

    private BiomechanicalParams mapBiomechanicsRow(ResultSet rs) throws SQLException {
        BiomechanicalParams b = new BiomechanicalParams();
        b.setId(rs.getInt("id"));
        b.setExamId(rs.getInt("exam_id"));
        b.setCcbi(rs.getBigDecimal("ccbi")); b.setCtbi(rs.getBigDecimal("ctbi"));
        b.setIsValue(rs.getBigDecimal("is_value")); b.setSpA1(rs.getBigDecimal("sp_a1"));
        b.setIntegrRadius(rs.getBigDecimal("integr_radius")); b.setArth(rs.getBigDecimal("arth"));
        b.setDaRatio(rs.getBigDecimal("da_ratio")); b.setSsi(rs.getBigDecimal("ssi"));
        return b;
    }
}
