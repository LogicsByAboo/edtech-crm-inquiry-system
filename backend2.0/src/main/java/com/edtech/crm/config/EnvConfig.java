package com.edtech.crm.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvConfig {

    private static final Logger log = LoggerFactory.getLogger(EnvConfig.class);

    public static void loadEnv() {
        File[] candidateFiles = new File[]{
                new File(".env"),
                new File("backend/.env"),
                new File("../.env")
        };

        for (File file : candidateFiles) {
            if (file.exists() && file.isFile()) {
                log.info("Loading environment configurations from {}", file.getAbsolutePath());
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String value = line.substring(eqIdx + 1).trim();
                            // Strip surrounding quotes if present
                            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                    (value.startsWith("'") && value.endsWith("'"))) {
                                value = value.substring(1, value.length() - 1);
                            }
                            if (System.getProperty(key) == null && System.getenv(key) == null) {
                                System.setProperty(key, value);
                            }
                        }
                    }
                } catch (IOException e) {
                    log.warn("Failed reading .env file: {}", e.getMessage());
                }
                break;
            }
        }

        // Process DATABASE_URL if present (Neon / Cloud PostgreSQL standard format)
        processDatabaseUrl();
    }

    private static void processDatabaseUrl() {
        String databaseUrl = System.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            databaseUrl = System.getenv("DATABASE_URL");
        }

        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }

        try {
            // Normalize postgresql / postgres scheme for URI parser
            String cleanUriStr = databaseUrl.trim();
            if (cleanUriStr.startsWith("jdbc:")) {
                cleanUriStr = cleanUriStr.substring(5);
            }

            URI uri = new URI(cleanUriStr);
            String userInfo = uri.getUserInfo();
            String host = uri.getHost();
            int port = uri.getPort() != -1 ? uri.getPort() : 5432;
            String path = uri.getPath();
            String query = uri.getQuery();

            if (userInfo != null) {
                String[] parts = userInfo.split(":", 2);
                String username = parts[0];
                String password = parts.length > 1 ? parts[1] : "";

                if (System.getProperty("DB_USERNAME") == null) {
                    System.setProperty("DB_USERNAME", username);
                    System.setProperty("spring.datasource.username", username);
                }
                if (System.getProperty("DB_PASSWORD") == null) {
                    System.setProperty("DB_PASSWORD", password);
                    System.setProperty("spring.datasource.password", password);
                }
            }

            // Construct valid PostgreSQL JDBC URL
            StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                    .append(host)
                    .append(":")
                    .append(port)
                    .append(path);

            if (query != null && !query.isBlank()) {
                jdbcUrl.append("?").append(query);
            }

            String finalJdbcUrl = jdbcUrl.toString();
            if (System.getProperty("DB_URL") == null) {
                System.setProperty("DB_URL", finalJdbcUrl);
                System.setProperty("spring.datasource.url", finalJdbcUrl);
            }

            log.info("Configured JDBC datasource parameters from DATABASE_URL (host: {}, database: {})", host, path);
        } catch (URISyntaxException e) {
            log.error("Failed to parse DATABASE_URL: {}", e.getMessage());
        }
    }
}
