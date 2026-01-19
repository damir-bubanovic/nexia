package com.nexia.core;

import com.nexia.core.domain.User;
import com.nexia.core.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiIT {

    private static final String API_BASE = "/api/v1/users";

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("nexia")
            .withUsername("nexia")
            .withPassword("nexia");

    @Container
    static final RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // JwtService requires >= 32 chars
        registry.add("nexia.security.jwt.secret", () -> "change-me-change-me-change-me-change-me");
        registry.add("nexia.security.jwt.issuer", () -> "nexia");
        registry.add("nexia.security.jwt.ttlSeconds", () -> "3600");

        // RabbitMQ (Testcontainers)
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbit.getMappedPort(5672)); // more version-stable than getAmqpPort()
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
    }

    @LocalServerPort
    int port;

    private final TestRestTemplate rest = new TestRestTemplate();

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private void ensureAdminExists(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) return;

        User admin = new User(
                UUID.randomUUID(),
                email.trim(),
                "IT Admin",
                Instant.now(),
                passwordEncoder.encode(rawPassword),
                "ADMIN"
        );
        userRepository.save(admin);
    }

    private String loginAndGetToken(String email, String password) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);

        String loginBody = """
                {
                  "email":"%s",
                  "password":"%s"
                }
                """.formatted(email, password);

        ResponseEntity<Map<String, Object>> login =
                rest.exchange(baseUrl() + "/api/auth/login", HttpMethod.POST, new HttpEntity<>(loginBody, h),
                        new ParameterizedTypeReference<>() {});

        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).isNotNull();

        Object token = login.getBody().get("accessToken");
        if (token == null) token = login.getBody().get("token");

        assertThat(token).isNotNull();
        return String.valueOf(token);
    }

    private HttpHeaders adminAuthHeaders() {
        String adminEmail = "admin.it@example.com";
        String adminPassword = "Password123!";

        ensureAdminExists(adminEmail, adminPassword);
        String token = loginAndGetToken(adminEmail, adminPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    void createUser_thenConflictOnDuplicateEmail() {
        HttpHeaders headers = adminAuthHeaders();

        HttpEntity<String> create = new HttpEntity<>(
                "{\"email\":\"it@example.com\",\"fullName\":\"IT User\"}",
                headers
        );

        ResponseEntity<Map<String, Object>> first =
                rest.exchange(baseUrl() + API_BASE, HttpMethod.POST, create,
                        new ParameterizedTypeReference<>() {});

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(first.getBody()).isNotNull();
        assertThat(first.getBody()).containsKey("id");

        ResponseEntity<Map<String, Object>> second =
                rest.exchange(baseUrl() + API_BASE, HttpMethod.POST, create,
                        new ParameterizedTypeReference<>() {});

        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(second.getBody()).isNotNull();
        assertThat(second.getBody()).containsKey("type");
        assertThat(second.getBody()).containsKey("status");
    }

    @Test
    void getByEmail_returnsUser() {
        HttpHeaders headers = adminAuthHeaders();

        HttpEntity<String> create = new HttpEntity<>(
                "{\"email\":\"lookup@example.com\",\"fullName\":\"Lookup User\"}",
                headers
        );

        ResponseEntity<Map<String, Object>> created =
                rest.exchange(baseUrl() + API_BASE, HttpMethod.POST, create,
                        new ParameterizedTypeReference<>() {});

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        HttpEntity<Void> getReq = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> fetched =
                rest.exchange(baseUrl() + API_BASE + "/by-email?email=lookup@example.com", HttpMethod.GET, getReq,
                        new ParameterizedTypeReference<>() {});

        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody()).isNotNull();
        assertThat(fetched.getBody()).containsEntry("email", "lookup@example.com");
        assertThat(fetched.getBody()).containsEntry("fullName", "Lookup User");
    }

    @Test
    void deleteUser_thenNotFoundOnGet() {
        HttpHeaders headers = adminAuthHeaders();

        HttpEntity<String> create = new HttpEntity<>(
                "{\"email\":\"delete@example.com\",\"fullName\":\"Delete User\"}",
                headers
        );

        ResponseEntity<Map<String, Object>> created =
                rest.exchange(baseUrl() + API_BASE, HttpMethod.POST, create,
                        new ParameterizedTypeReference<>() {});

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();

        String id = String.valueOf(created.getBody().get("id"));
        assertThat(id).isNotBlank();

        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<Void> deleted =
                rest.exchange(baseUrl() + API_BASE + "/" + id, HttpMethod.DELETE, req, Void.class);

        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Map<String, Object>> getAfterDelete =
                rest.exchange(baseUrl() + API_BASE + "/" + id, HttpMethod.GET, req,
                        new ParameterizedTypeReference<>() {});

        assertThat(getAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getAfterDelete.getBody()).isNotNull();
        assertThat(getAfterDelete.getBody()).containsKey("type");
        assertThat(getAfterDelete.getBody()).containsEntry("status", 404);
    }
}
