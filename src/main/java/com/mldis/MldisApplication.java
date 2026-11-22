package com.mldis;

import com.mldis.model.*;
import com.mldis.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MldisApplication {
    public static void main(String[] args) { SpringApplication.run(MldisApplication.class, args); }

    @Bean
    CommandLineRunner seed(StaffUserRepository users, DepartmentRepository departments, MedicineRepository medicines, PatientRepository patients) {
        return args -> {
            if (users.count() == 0) {
                users.save(new StaffUser("Administrator", "admin", "admin123", "ADMIN", "Administration"));
                users.save(new StaffUser("Registration Desk", "reception", "desk123", "REGISTRATION", "OPD"));
                users.save(new StaffUser("Dr. Meera Das", "doctor", "doctor123", "DOCTOR", "General Medicine"));
                users.save(new StaffUser("Lab Technician", "lab", "lab123", "TECHNICIAN", "Laboratory"));
                users.save(new StaffUser("Dispensary Staff", "pharmacy", "pharmacy123", "PHARMACY", "Dispensary"));
            }
            if (departments.count() == 0) {
                departments.save(new Department("General Medicine", "OPD consultations"));
                departments.save(new Department("Radiology", "Imaging investigations"));
                departments.save(new Department("Pathology", "Pathology investigations"));
                departments.save(new Department("Laboratory", "Clinical laboratory"));
                departments.save(new Department("Emergency", "Emergency services"));
            }
            if (patients.findByPatientCode("PT-DEMO-001").isEmpty()) {
                patients.save(new Patient("PT-DEMO-001", "Demo Patient", "9000000000", "patient@example.com",
                    "Not specified", "2000-01-01", "Demo address", "General Medicine", "Dr. Meera Das",
                    "Sample account for demonstrating the patient portal. Do not use real patient information."));
            }
            if (users.findByUsername("patient").isEmpty()) {
                StaffUser patientLogin = new StaffUser("Demo Patient", "patient", "patient123", "PATIENT", "Patient Portal");
                patientLogin.setLinkedPatientCode("PT-DEMO-001");
                users.save(patientLogin);
            }
            if (medicines.count() == 0) {
                medicines.save(new Medicine("MED001", "Paracetamol 500mg", "BATCH-A1", 100, 2.50, "2027-12-31"));
                medicines.save(new Medicine("MED002", "Amoxicillin 500mg", "BATCH-B2", 50, 8.00, "2027-06-30"));
                medicines.save(new Medicine("MED003", "ORS Sachet", "BATCH-C3", 80, 12.00, "2028-03-31"));
            }
        };
    }
}
