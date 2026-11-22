package com.mldis.model;
import jakarta.persistence.*;
@Entity
public class Medicine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String medicineCode;
    private String name;
    private String batchNo;
    private int quantity;
    private double unitPrice;
    private String expiryDate;
    public Medicine() {}
    public Medicine(String medicineCode, String name, String batchNo, int quantity, double unitPrice, String expiryDate) {
        this.medicineCode = medicineCode;
        this.name = name;
        this.batchNo = batchNo;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.expiryDate = expiryDate;
    }
    public Long getId() { return id; }
    public String getMedicineCode() { return medicineCode; }
    public void setMedicineCode(String value) { this.medicineCode = value; }
    public String getName() { return name; }
    public void setName(String value) { this.name = value; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String value) { this.batchNo = value; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int value) { this.quantity = value; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double value) { this.unitPrice = value; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String value) { this.expiryDate = value; }
}
