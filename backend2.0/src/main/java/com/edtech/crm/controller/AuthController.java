package com.edtech.crm.controller;

import com.edtech.crm.dto.AdminProfileResponse;
import com.edtech.crm.dto.AuthRequest;
import com.edtech.crm.dto.AuthResponse;
import com.edtech.crm.service.AuthService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AdminProfileResponse> getCurrentAdmin(Principal principal) {
        AdminProfileResponse response = authService.getCurrentAdminProfile(principal.getName());
        return ResponseEntity.ok(response);
    }
}
