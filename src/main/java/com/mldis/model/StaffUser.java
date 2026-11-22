package com.mldis.model;
import jakarta.persistence.*;
@Entity
public class StaffUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String username;
    private String password;
    private String role;
    private String department;
    private String linkedPatientCode;
    public StaffUser() {}
    public StaffUser(String fullName, String username, String password, String role, String department) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.department = department;
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String value) { this.fullName = value; }
    public String getUsername() { return username; }
    public void setUsername(String value) { this.username = value; }
    public String getPassword() { return password; }
    public void setPassword(String value) { this.password = value; }
    public String getRole() { return role; }
    public void setRole(String value) { this.role = value; }
    public String getLinkedPatientCode() { return linkedPatientCode; }
    public void setLinkedPatientCode(String value) { this.linkedPatientCode = value; }
    public String getDepartment() { return department; }
    public void setDepartment(String value) { this.department = value; }
}
