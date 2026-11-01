package com.edtech.crm.service;

import com.edtech.crm.dto.AdminProfileResponse;
import com.edtech.crm.dto.AuthRequest;
import com.edtech.crm.dto.AuthResponse;
import com.edtech.crm.entity.Admin;
import com.edtech.crm.exception.ResourceNotFoundException;
import com.edtech.crm.repository.AdminRepository;
import com.edtech.crm.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        Admin admin = adminRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(admin.getId(), admin.getEmail(), admin.getName());
        long expiresIn = tokenProvider.getExpirationMs();

        AdminProfileResponse profile = new AdminProfileResponse(admin.getId(), admin.getName(), admin.getEmail());
        return new AuthResponse(token, expiresIn, profile);
    }

    @Transactional(readOnly = true)
    public AdminProfileResponse getCurrentAdminProfile(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with email: " + email));
        return new AdminProfileResponse(admin.getId(), admin.getName(), admin.getEmail());
    }
}
