package com.mldis.model;
import jakarta.persistence.*;
@Entity
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    public Department() {}
    public Department(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String value) { this.name = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }
}
