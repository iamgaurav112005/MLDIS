package com.mldis.model;
import jakarta.persistence.*;
@Entity
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String billNumber;
    private String patientCode;
    private String patientName;
    private String visitType;
    private double consultationCharge;
    private double testCharge;
    private double medicineCharge;
    private double discount;
    private double taxPercent;
    private double total;
    private String status;
    public Bill() {}
    public Bill(String billNumber, String patientCode, String patientName, String visitType, double consultationCharge, double testCharge, double medicineCharge, double discount, double taxPercent, double total, String status) {
        this.billNumber = billNumber;
        this.patientCode = patientCode;
        this.patientName = patientName;
        this.visitType = visitType;
        this.consultationCharge = consultationCharge;
        this.testCharge = testCharge;
        this.medicineCharge = medicineCharge;
        this.discount = discount;
        this.taxPercent = taxPercent;
        this.total = total;
        this.status = status;
    }
    public Long getId() { return id; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String value) { this.billNumber = value; }
    public String getPatientCode() { return patientCode; }
    public void setPatientCode(String value) { this.patientCode = value; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String value) { this.patientName = value; }
    public String getVisitType() { return visitType; }
    public void setVisitType(String value) { this.visitType = value; }
    public double getConsultationCharge() { return consultationCharge; }
    public void setConsultationCharge(double value) { this.consultationCharge = value; }
    public double getTestCharge() { return testCharge; }
    public void setTestCharge(double value) { this.testCharge = value; }
    public double getMedicineCharge() { return medicineCharge; }
    public void setMedicineCharge(double value) { this.medicineCharge = value; }
    public double getDiscount() { return discount; }
    public void setDiscount(double value) { this.discount = value; }
    public double getTaxPercent() { return taxPercent; }
    public void setTaxPercent(double value) { this.taxPercent = value; }
    public double getTotal() { return total; }
    public void setTotal(double value) { this.total = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
}
