package com.cornea.management.entity;

import java.math.BigDecimal;

public class CornealTopography {

    private Integer id;
    private Integer examId;
    private BigDecimal frontRf, frontRs, frontRm;
    private BigDecimal frontK1, frontK2, frontKm;
    private BigDecimal frontQVal, frontRper, frontRmin, frontAxis, frontAstig;
    private BigDecimal backRf, backRs, backRm;
    private BigDecimal backK1, backK2, backKm;
    private BigDecimal backQVal, backRper, backRmin, backAxis, backAstig;
    private BigDecimal pupilCenterPachyX, pupilCenterPachyY;
    private BigDecimal pachyApexX, pachyApexY;
    private BigDecimal thinnestLocatPachyX, thinnestLocatPachyY;
    private BigDecimal kMaxPachyX, kMaxPachyY;
    private BigDecimal corneaVolume, chamberVolume, angle, acDepth, pupilDia, iop, lensTh;

    public CornealTopography() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getExamId() { return examId; }
    public void setExamId(Integer examId) { this.examId = examId; }

    public BigDecimal getFrontRf() { return frontRf; }
    public void setFrontRf(BigDecimal frontRf) { this.frontRf = frontRf; }
    public BigDecimal getFrontRs() { return frontRs; }
    public void setFrontRs(BigDecimal frontRs) { this.frontRs = frontRs; }
    public BigDecimal getFrontRm() { return frontRm; }
    public void setFrontRm(BigDecimal frontRm) { this.frontRm = frontRm; }
    public BigDecimal getFrontK1() { return frontK1; }
    public void setFrontK1(BigDecimal frontK1) { this.frontK1 = frontK1; }
    public BigDecimal getFrontK2() { return frontK2; }
    public void setFrontK2(BigDecimal frontK2) { this.frontK2 = frontK2; }
    public BigDecimal getFrontKm() { return frontKm; }
    public void setFrontKm(BigDecimal frontKm) { this.frontKm = frontKm; }
    public BigDecimal getFrontQVal() { return frontQVal; }
    public void setFrontQVal(BigDecimal frontQVal) { this.frontQVal = frontQVal; }
    public BigDecimal getFrontRper() { return frontRper; }
    public void setFrontRper(BigDecimal frontRper) { this.frontRper = frontRper; }
    public BigDecimal getFrontRmin() { return frontRmin; }
    public void setFrontRmin(BigDecimal frontRmin) { this.frontRmin = frontRmin; }
    public BigDecimal getFrontAxis() { return frontAxis; }
    public void setFrontAxis(BigDecimal frontAxis) { this.frontAxis = frontAxis; }
    public BigDecimal getFrontAstig() { return frontAstig; }
    public void setFrontAstig(BigDecimal frontAstig) { this.frontAstig = frontAstig; }

    public BigDecimal getBackRf() { return backRf; }
    public void setBackRf(BigDecimal backRf) { this.backRf = backRf; }
    public BigDecimal getBackRs() { return backRs; }
    public void setBackRs(BigDecimal backRs) { this.backRs = backRs; }
    public BigDecimal getBackRm() { return backRm; }
    public void setBackRm(BigDecimal backRm) { this.backRm = backRm; }
    public BigDecimal getBackK1() { return backK1; }
    public void setBackK1(BigDecimal backK1) { this.backK1 = backK1; }
    public BigDecimal getBackK2() { return backK2; }
    public void setBackK2(BigDecimal backK2) { this.backK2 = backK2; }
    public BigDecimal getBackKm() { return backKm; }
    public void setBackKm(BigDecimal backKm) { this.backKm = backKm; }
    public BigDecimal getBackQVal() { return backQVal; }
    public void setBackQVal(BigDecimal backQVal) { this.backQVal = backQVal; }
    public BigDecimal getBackRper() { return backRper; }
    public void setBackRper(BigDecimal backRper) { this.backRper = backRper; }
    public BigDecimal getBackRmin() { return backRmin; }
    public void setBackRmin(BigDecimal backRmin) { this.backRmin = backRmin; }
    public BigDecimal getBackAxis() { return backAxis; }
    public void setBackAxis(BigDecimal backAxis) { this.backAxis = backAxis; }
    public BigDecimal getBackAstig() { return backAstig; }
    public void setBackAstig(BigDecimal backAstig) { this.backAstig = backAstig; }

    public BigDecimal getPupilCenterPachyX() { return pupilCenterPachyX; }
    public void setPupilCenterPachyX(BigDecimal pupilCenterPachyX) { this.pupilCenterPachyX = pupilCenterPachyX; }
    public BigDecimal getPupilCenterPachyY() { return pupilCenterPachyY; }
    public void setPupilCenterPachyY(BigDecimal pupilCenterPachyY) { this.pupilCenterPachyY = pupilCenterPachyY; }
    public BigDecimal getPachyApexX() { return pachyApexX; }
    public void setPachyApexX(BigDecimal pachyApexX) { this.pachyApexX = pachyApexX; }
    public BigDecimal getPachyApexY() { return pachyApexY; }
    public void setPachyApexY(BigDecimal pachyApexY) { this.pachyApexY = pachyApexY; }
    public BigDecimal getThinnestLocatPachyX() { return thinnestLocatPachyX; }
    public void setThinnestLocatPachyX(BigDecimal thinnestLocatPachyX) { this.thinnestLocatPachyX = thinnestLocatPachyX; }
    public BigDecimal getThinnestLocatPachyY() { return thinnestLocatPachyY; }
    public void setThinnestLocatPachyY(BigDecimal thinnestLocatPachyY) { this.thinnestLocatPachyY = thinnestLocatPachyY; }
    public BigDecimal getKMaxPachyX() { return kMaxPachyX; }
    public void setKMaxPachyX(BigDecimal kMaxPachyX) { this.kMaxPachyX = kMaxPachyX; }
    public BigDecimal getKMaxPachyY() { return kMaxPachyY; }
    public void setKMaxPachyY(BigDecimal kMaxPachyY) { this.kMaxPachyY = kMaxPachyY; }

    public BigDecimal getCorneaVolume() { return corneaVolume; }
    public void setCorneaVolume(BigDecimal corneaVolume) { this.corneaVolume = corneaVolume; }
    public BigDecimal getChamberVolume() { return chamberVolume; }
    public void setChamberVolume(BigDecimal chamberVolume) { this.chamberVolume = chamberVolume; }
    public BigDecimal getAngle() { return angle; }
    public void setAngle(BigDecimal angle) { this.angle = angle; }
    public BigDecimal getAcDepth() { return acDepth; }
    public void setAcDepth(BigDecimal acDepth) { this.acDepth = acDepth; }
    public BigDecimal getPupilDia() { return pupilDia; }
    public void setPupilDia(BigDecimal pupilDia) { this.pupilDia = pupilDia; }
    public BigDecimal getIop() { return iop; }
    public void setIop(BigDecimal iop) { this.iop = iop; }
    public BigDecimal getLensTh() { return lensTh; }
    public void setLensTh(BigDecimal lensTh) { this.lensTh = lensTh; }
}
