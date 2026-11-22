package com.mldis.api;

import com.mldis.model.StaffUser;
import com.mldis.repo.StaffUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final StaffUserRepository users;
    public AuthController(StaffUserRepository users) { this.users = users; }

    private Map<String, Object> profile(StaffUser user) {
        return Map.of(
            "username", user.getUsername(),
            "fullName", user.getFullName() == null ? user.getUsername() : user.getFullName(),
            "role", user.getRole(),
            "linkedPatientCode", user.getLinkedPatientCode() == null ? "" : user.getLinkedPatientCode()
        );
    }

    @PostMapping("/login")
    public Object login(@RequestBody Map<String,String> body, HttpSession session) {
        StaffUser user = users.findByUsername(body.get("username")).orElse(null);
        if (user == null || !user.getPassword().equals(body.get("password"))) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
        session.setAttribute("user", user.getUsername());
        session.setAttribute("role", user.getRole());
        session.setAttribute("linkedPatientCode", user.getLinkedPatientCode());
        return profile(user);
    }

    @GetMapping("/me")
    public Object me(HttpSession session) {
        if (session.getAttribute("user") == null) return Map.of("authenticated", false);
        StaffUser user = users.findByUsername((String) session.getAttribute("user")).orElse(null);
        if (user == null) return Map.of("authenticated", false);
        Map<String, Object> data = new java.util.HashMap<>(profile(user));
        data.put("authenticated", true);
        return data;
    }

    @PostMapping("/logout")
    public Object logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "Logged out");
    }
}
