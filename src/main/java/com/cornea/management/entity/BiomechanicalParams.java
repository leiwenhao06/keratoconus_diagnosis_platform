package com.cornea.management.entity;

import java.math.BigDecimal;

public class BiomechanicalParams {

    private Integer id;
    private Integer examId;
    private BigDecimal ccbi;
    private BigDecimal ctbi;
    private BigDecimal isValue;
    private BigDecimal spA1;
    private BigDecimal integrRadius;
    private BigDecimal arth;
    private BigDecimal daRatio;
    private BigDecimal ssi;

    public BiomechanicalParams() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getExamId() { return examId; }
    public void setExamId(Integer examId) { this.examId = examId; }

    public BigDecimal getCcbi() { return ccbi; }
    public void setCcbi(BigDecimal ccbi) { this.ccbi = ccbi; }
    public BigDecimal getCtbi() { return ctbi; }
    public void setCtbi(BigDecimal ctbi) { this.ctbi = ctbi; }
    public BigDecimal getIsValue() { return isValue; }
    public void setIsValue(BigDecimal isValue) { this.isValue = isValue; }
    public BigDecimal getSpA1() { return spA1; }
    public void setSpA1(BigDecimal spA1) { this.spA1 = spA1; }
    public BigDecimal getIntegrRadius() { return integrRadius; }
    public void setIntegrRadius(BigDecimal integrRadius) { this.integrRadius = integrRadius; }
    public BigDecimal getArth() { return arth; }
    public void setArth(BigDecimal arth) { this.arth = arth; }
    public BigDecimal getDaRatio() { return daRatio; }
    public void setDaRatio(BigDecimal daRatio) { this.daRatio = daRatio; }
    public BigDecimal getSsi() { return ssi; }
    public void setSsi(BigDecimal ssi) { this.ssi = ssi; }
}
