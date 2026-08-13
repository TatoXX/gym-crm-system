package org.epam.gym_crm_system1.config;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProfileConfigurationTest {

    @Test
    void applicationProperties_ShouldActivateLocalProfileByDefault() throws Exception {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            assertNotNull(inputStream);
            properties.load(inputStream);
        }

        assertEquals("local", properties.getProperty("spring.profiles.active"));
    }

    @Test
    void localProfile_ShouldContainLocalDatabaseUrl() throws Exception {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("application-local.properties")) {

            assertNotNull(inputStream);
            properties.load(inputStream);
        }

        assertEquals(
                "jdbc:postgresql://localhost:5432/gym_crm",
                properties.getProperty("spring.datasource.url")
        );
    }
}