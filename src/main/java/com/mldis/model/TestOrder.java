package com.mldis.model;
import jakarta.persistence.*;
@Entity
public class TestOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String patientCode;
    private String patientName;
    private String testType;
    private String testName;
    private String prescribedBy;
    private String status;
    private String result;
    private double fee;
    public TestOrder() {}
    public TestOrder(String patientCode, String patientName, String testType, String testName, String prescribedBy, String status, String result, double fee) {
        this.patientCode = patientCode;
        this.patientName = patientName;
        this.testType = testType;
        this.testName = testName;
        this.prescribedBy = prescribedBy;
        this.status = status;
        this.result = result;
        this.fee = fee;
    }
    public Long getId() { return id; }
    public String getPatientCode() { return patientCode; }
    public void setPatientCode(String value) { this.patientCode = value; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String value) { this.patientName = value; }
    public String getTestType() { return testType; }
    public void setTestType(String value) { this.testType = value; }
    public String getTestName() { return testName; }
    public void setTestName(String value) { this.testName = value; }
    public String getPrescribedBy() { return prescribedBy; }
    public void setPrescribedBy(String value) { this.prescribedBy = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public String getResult() { return result; }
    public void setResult(String value) { this.result = value; }
    public double getFee() { return fee; }
    public void setFee(double value) { this.fee = value; }
}
