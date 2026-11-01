package com.edtech.crm.service;

import com.edtech.crm.entity.Admin;
import com.edtech.crm.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AdminBootstrapService {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapService.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.bootstrap.name:Administrator}")
    private String adminName;

    @Value("${app.admin.bootstrap.email:}")
    private String adminEmail;

    @Value("${app.admin.bootstrap.password:}")
    private String adminPassword;

    public AdminBootstrapService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void bootstrapAdmin() {
        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            log.info("Admin bootstrap credentials not configured (ADMIN_EMAIL or ADMIN_PASSWORD empty). Skipping bootstrap.");
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();

        if (adminRepository.existsByEmail(normalizedEmail)) {
            log.info("Admin account with email '{}' already exists. Preserving existing account (idempotent bootstrap).", normalizedEmail);
            return;
        }

        String displayName = StringUtils.hasText(adminName) ? adminName.trim() : "Administrator";
        String hashedPassword = passwordEncoder.encode(adminPassword.trim());

        Admin admin = new Admin(displayName, normalizedEmail, hashedPassword);
        adminRepository.save(admin);

        log.info("Initial admin successfully bootstrapped with email '{}'.", normalizedEmail);
    }
}
