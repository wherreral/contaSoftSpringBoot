package com.hp.contaSoft.report.monthly;

import java.util.ArrayList;
import java.util.List;

public class MonthlyClientSummaryReportData {

    private String clientName;
    private String clientRut;
    private String periodLabel;
    private String generatedAt;
    private String generatedBy;
    private int workersCount;

    private double totalImponible;
    private double totalNoImponible;
    private double totalHaber;
    private double totalDctoPrevisional;
    private double totalDctoPersonal;
    private double totalDescuentos;
    private double totalAlcanceLiquido;

    private List<MonthlyClientSummaryRow> rows = new ArrayList<MonthlyClientSummaryRow>();

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientRut() {
        return clientRut;
    }

    public void setClientRut(String clientRut) {
        this.clientRut = clientRut;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public int getWorkersCount() {
        return workersCount;
    }

    public void setWorkersCount(int workersCount) {
        this.workersCount = workersCount;
    }

    public double getTotalImponible() {
        return totalImponible;
    }

    public void setTotalImponible(double totalImponible) {
        this.totalImponible = totalImponible;
    }

    public double getTotalNoImponible() {
        return totalNoImponible;
    }

    public void setTotalNoImponible(double totalNoImponible) {
        this.totalNoImponible = totalNoImponible;
    }

    public double getTotalHaber() {
        return totalHaber;
    }

    public void setTotalHaber(double totalHaber) {
        this.totalHaber = totalHaber;
    }

    public double getTotalDctoPrevisional() {
        return totalDctoPrevisional;
    }

    public void setTotalDctoPrevisional(double totalDctoPrevisional) {
        this.totalDctoPrevisional = totalDctoPrevisional;
    }

    public double getTotalDctoPersonal() {
        return totalDctoPersonal;
    }

    public void setTotalDctoPersonal(double totalDctoPersonal) {
        this.totalDctoPersonal = totalDctoPersonal;
    }

    public double getTotalDescuentos() {
        return totalDescuentos;
    }

    public void setTotalDescuentos(double totalDescuentos) {
        this.totalDescuentos = totalDescuentos;
    }

    public double getTotalAlcanceLiquido() {
        return totalAlcanceLiquido;
    }

    public void setTotalAlcanceLiquido(double totalAlcanceLiquido) {
        this.totalAlcanceLiquido = totalAlcanceLiquido;
    }

    public List<MonthlyClientSummaryRow> getRows() {
        return rows;
    }

    public void setRows(List<MonthlyClientSummaryRow> rows) {
        this.rows = rows;
    }
}

