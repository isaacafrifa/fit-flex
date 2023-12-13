package iam.fitflex;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractBaseContainer {

    public static PostgreSQLContainer POSTGRESQL_CONTAINER;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer("postgres:15.0")
                .withDatabaseName("flex_db");
        POSTGRESQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

}
