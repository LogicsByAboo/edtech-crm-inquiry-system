package com.edtech.crm;

import com.edtech.crm.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EdTechCrmApplication {

    static {
        // Load .env variables if available prior to Spring context initialization
        EnvConfig.loadEnv();
    }

    public static void main(String[] args) {
        SpringApplication.run(EdTechCrmApplication.class, args);
    }
}
