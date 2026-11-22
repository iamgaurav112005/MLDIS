package com.mldis.api;

import com.mldis.model.*;
import com.mldis.repo.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final PatientRepository patients;
    private final DepartmentRepository departments;
    private final MedicineRepository medicines;
    private final TestOrderRepository tests;
    private final BillRepository bills;
    private final StaffUserRepository users;

    public ApiController(PatientRepository patients, DepartmentRepository departments, MedicineRepository medicines,
                         TestOrderRepository tests, BillRepository bills, StaffUserRepository users) {
        this.patients=patients; this.departments=departments; this.medicines=medicines;
        this.tests=tests; this.bills=bills; this.users=users;
    }

    private boolean loggedIn(HttpSession s) { return s.getAttribute("user") != null; }
    private boolean hasRole(HttpSession s, String... roles) {
        if (!loggedIn(s)) return false;
        String role = String.valueOf(s.getAttribute("role"));
        return Arrays.asList(roles).contains(role);
    }
    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("error","Please sign in first"));
    }
    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(Map.of("error","Your account does not have permission for this action"));
    }
    private ResponseEntity<?> denied(HttpSession s, String... roles) {
        return loggedIn(s) ? forbidden() : unauthorized();
    }

    @GetMapping("/dashboard")
    public Object dashboard(HttpSession s) {
        if (!loggedIn(s)) return unauthorized();
        long low = medicines.findAll().stream().filter(m -> m.getQuantity() <= 10).count();
        return Map.of("patients",patients.count(),"tests",tests.count(),"medicines",medicines.count(),
            "bills",bills.count(),"lowStock",low,
            "pendingTests",tests.findAll().stream().filter(t -> !"COMPLETED".equals(t.getStatus())).count());
    }

    @GetMapping("/patient/portal")
    public Object patientPortal(HttpSession s) {
        if (!hasRole(s, "PATIENT")) return denied(s, "PATIENT");
        String code = (String) s.getAttribute("linkedPatientCode");
        if (code == null || code.isBlank()) return ResponseEntity.status(404).body(Map.of("error","No patient record is linked to this login. Contact the registration desk."));
        Patient patient = patients.findByPatientCode(code).orElse(null);
        if (patient == null) return ResponseEntity.status(404).body(Map.of("error","Linked patient record was not found."));
        return Map.of("patient", patient, "tests", tests.findByPatientCode(code), "bills", bills.findByPatientCode(code));
    }

    @GetMapping("/patients")
    public Object allPatients(HttpSession s) {
        return hasRole(s,"ADMIN","REGISTRATION","DOCTOR") ? patients.findAll() : denied(s,"ADMIN","REGISTRATION","DOCTOR");
    }
    @PostMapping("/patients")
    public Object addPatient(@RequestBody Patient p, HttpSession s) {
        if(!hasRole(s,"ADMIN","REGISTRATION")) return denied(s,"ADMIN","REGISTRATION");
        if(p.getPatientCode()==null || p.getPatientCode().isBlank()) p.setPatientCode("PT-"+System.currentTimeMillis());
        if(patients.findByPatientCode(p.getPatientCode()).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","Patient code already exists"));
        return patients.save(p);
    }

    @GetMapping("/departments")
    public Object allDepartments(HttpSession s) { return loggedIn(s)?departments.findAll():unauthorized(); }
    @PostMapping("/departments")
    public Object addDepartment(@RequestBody Department d,HttpSession s) {
        return hasRole(s,"ADMIN")?departments.save(d):denied(s,"ADMIN");
    }

    @GetMapping("/medicines")
    public Object allMedicines(HttpSession s) {
        return hasRole(s,"ADMIN","PHARMACY")?medicines.findAll():denied(s,"ADMIN","PHARMACY");
    }
    @PostMapping("/medicines")
    public Object addMedicine(@RequestBody Medicine m,HttpSession s) {
        if(!hasRole(s,"ADMIN","PHARMACY")) return denied(s,"ADMIN","PHARMACY");
        if(m.getMedicineCode()==null || m.getMedicineCode().isBlank()) m.setMedicineCode("MED-"+System.currentTimeMillis());
        return medicines.save(m);
    }
    @PutMapping("/medicines/{id}/stock")
    public Object stock(@PathVariable Long id,@RequestBody Map<String,Integer> body,HttpSession s) {
        if(!hasRole(s,"ADMIN","PHARMACY")) return denied(s,"ADMIN","PHARMACY");
        Medicine m=medicines.findById(id).orElse(null);
        if(m==null) return ResponseEntity.notFound().build();
        int q=body.getOrDefault("quantity",0);
        if(m.getQuantity()+q<0) return ResponseEntity.badRequest().body(Map.of("error","Insufficient stock"));
        m.setQuantity(m.getQuantity()+q); return medicines.save(m);
    }

    @GetMapping("/tests")
    public Object allTests(HttpSession s) {
        return hasRole(s,"ADMIN","DOCTOR","TECHNICIAN")?tests.findAll():denied(s,"ADMIN","DOCTOR","TECHNICIAN");
    }
    @PostMapping("/tests")
    public Object addTest(@RequestBody TestOrder t,HttpSession s) {
        if(!hasRole(s,"ADMIN","DOCTOR")) return denied(s,"ADMIN","DOCTOR");
        if(t.getStatus()==null || t.getStatus().isBlank()) t.setStatus("PENDING");
        return tests.save(t);
    }
    @PutMapping("/tests/{id}")
    public Object updateTest(@PathVariable Long id,@RequestBody Map<String,String> body,HttpSession s) {
        if(!hasRole(s,"ADMIN","TECHNICIAN")) return denied(s,"ADMIN","TECHNICIAN");
        TestOrder t=tests.findById(id).orElse(null);
        if(t==null) return ResponseEntity.notFound().build();
        if(body.containsKey("status")) t.setStatus(body.get("status"));
        if(body.containsKey("result")) t.setResult(body.get("result"));
        return tests.save(t);
    }

    @GetMapping("/bills")
    public Object allBills(HttpSession s) {
        return hasRole(s,"ADMIN","REGISTRATION")?bills.findAll():denied(s,"ADMIN","REGISTRATION");
    }
    @PostMapping("/bills")
    public Object addBill(@RequestBody Bill b,HttpSession s) {
        if(!hasRole(s,"ADMIN","REGISTRATION")) return denied(s,"ADMIN","REGISTRATION");
        double subtotal=b.getConsultationCharge()+b.getTestCharge()+b.getMedicineCharge()-b.getDiscount();
        if(subtotal<0) subtotal=0;
        b.setTotal(subtotal+(subtotal*b.getTaxPercent()/100.0));
        if(b.getBillNumber()==null || b.getBillNumber().isBlank()) b.setBillNumber("INV-"+System.currentTimeMillis());
        if(b.getStatus()==null) b.setStatus("PAID");
        return bills.save(b);
    }

    @GetMapping("/staff")
    public Object allStaff(HttpSession s) {
        if(!hasRole(s,"ADMIN")) return denied(s,"ADMIN");
        return users.findAll().stream().map(u -> {
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("id",u.getId()); row.put("fullName",u.getFullName()); row.put("username",u.getUsername());
            row.put("role",u.getRole()); row.put("department",u.getDepartment());
            row.put("linkedPatientCode",u.getLinkedPatientCode()==null?"":u.getLinkedPatientCode());
            return row;
        }).toList();
    }
    @PostMapping("/staff")
    public Object addStaff(@RequestBody StaffUser u,HttpSession s) {
        if(!hasRole(s,"ADMIN")) return denied(s,"ADMIN");
        if(users.findByUsername(u.getUsername()).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","Username already exists"));
        if("PATIENT".equals(u.getRole()) && (u.getLinkedPatientCode()==null || u.getLinkedPatientCode().isBlank()))
            return ResponseEntity.badRequest().body(Map.of("error","Patient accounts must be linked to a Patient ID"));
        if("PATIENT".equals(u.getRole()) && patients.findByPatientCode(u.getLinkedPatientCode()).isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error","Patient ID does not exist"));
        return users.save(u);
    }
}
