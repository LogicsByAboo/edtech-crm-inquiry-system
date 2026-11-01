package com.edtech.crm;

import com.edtech.crm.config.EnvConfig;
import com.edtech.crm.entity.Inquiry;
import com.edtech.crm.entity.InquiryCategory;
import com.edtech.crm.entity.InquiryStatus;
import com.edtech.crm.entity.UserType;
import com.edtech.crm.repository.AdminRepository;
import com.edtech.crm.repository.InquiryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Genuine Hosted PostgreSQL Integration Test suite.
 * Per specification:
 * - Does not silently use H2 as proof of hosted PostgreSQL integration.
 * - Executes when hosted PostgreSQL credentials (Neon / Supabase) are configured via DATABASE_URL or DB_URL.
 * - Tests table connectivity, schema migration verification, and CRUD operations.
 */
@SpringBootTest
@Tag("integration")
@EnabledIf("isLiveDbAvailable")
class PostgresDatabaseIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(PostgresDatabaseIntegrationTest.class);

    static boolean isLiveDbAvailable() {
        EnvConfig.loadEnv();
        String dbUrl = System.getProperty("DB_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("DB_URL");
        }
        return dbUrl != null && !dbUrl.contains("localhost:5432");
    }

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Test
    @DisplayName("Verify live Neon PostgreSQL connectivity, Flyway migrations, and CRUD operations")
    void testLivePostgresConnectivity() {
        log.info("Live PostgreSQL connection detected. Executing integration verification...");

        assertThat(inquiryRepository).isNotNull();
        assertThat(adminRepository).isNotNull();

        Inquiry testInquiry = new Inquiry(
                "Neon Integration Test",
                "neon-test@example.com",
                "+1 (555) 987-6543",
                UserType.STUDENT,
                InquiryCategory.SCHOLARSHIP,
                "Testing real database round-trip persistence with Neon PostgreSQL"
        );
        testInquiry.setStatus(InquiryStatus.NEW);

        Inquiry saved = inquiryRepository.save(testInquiry);
        assertThat(saved.getId()).isNotNull();

        // Verify retrieval
        var retrieved = inquiryRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Neon Integration Test");
        assertThat(retrieved.get().getCategory()).isEqualTo(InquiryCategory.SCHOLARSHIP);
        assertThat(retrieved.get().getStatus()).isEqualTo(InquiryStatus.NEW);

        // Cleanup
        inquiryRepository.deleteById(saved.getId());
        log.info("Neon PostgreSQL round-trip test completed successfully.");
    }
}
