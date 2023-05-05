package com.nicklastrange.strongmanbot;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {AbstractTestContainersIntegrationTest.Initializer.class})
public class AbstractTestContainersIntegrationTest {

    @Container
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "spring.flyway.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.flyway.user=" + postgreSQLContainer.getUsername(),
                "spring.flyway.password=" + postgreSQLContainer.getPassword(),
                "spring.r2dbc.url=" + postgreSQLContainer.getJdbcUrl().replaceAll("jdbc", "r2dbc"),
                "spring.r2dbc.username=" + postgreSQLContainer.getUsername(),
                "spring.r2dbc.password=" + postgreSQLContainer.getPassword()
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
