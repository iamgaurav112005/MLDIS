package com.mldis.model;
import jakarta.persistence.*;
@Entity
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String patientCode;
    private String fullName;
    private String phone;
    private String email;
    private String gender;
    private String dateOfBirth;
    private String address;
    private String department;
    private String doctor;
    private String medicalHistory;
    public Patient() {}
    public Patient(String patientCode, String fullName, String phone, String email, String gender, String dateOfBirth, String address, String department, String doctor, String medicalHistory) {
        this.patientCode = patientCode;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.department = department;
        this.doctor = doctor;
        this.medicalHistory = medicalHistory;
    }
    public Long getId() { return id; }
    public String getPatientCode() { return patientCode; }
    public void setPatientCode(String value) { this.patientCode = value; }
    public String getFullName() { return fullName; }
    public void setFullName(String value) { this.fullName = value; }
    public String getPhone() { return phone; }
    public void setPhone(String value) { this.phone = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
    public String getGender() { return gender; }
    public void setGender(String value) { this.gender = value; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String value) { this.dateOfBirth = value; }
    public String getAddress() { return address; }
    public void setAddress(String value) { this.address = value; }
    public String getDepartment() { return department; }
    public void setDepartment(String value) { this.department = value; }
    public String getDoctor() { return doctor; }
    public void setDoctor(String value) { this.doctor = value; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String value) { this.medicalHistory = value; }
}
