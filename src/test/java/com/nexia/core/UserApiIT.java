package com.nexia.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.core.ParameterizedTypeReference;


import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiIT {

    @SuppressWarnings("resource")
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("nexia")
            .withUsername("nexia")
            .withPassword("nexia");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    int port;

    private final TestRestTemplate rest = new TestRestTemplate();

    @Test
    void createUser_thenConflictOnDuplicateEmail() {
        String base = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> create = new HttpEntity<>(
                "{\"email\":\"it@example.com\",\"fullName\":\"IT User\"}",
                headers
        );

        ResponseEntity<Map<String, Object>> first =
                rest.exchange(base + "/api/users", HttpMethod.POST, create,
                        new ParameterizedTypeReference<>() {});

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(first.getBody()).isNotNull();
        assertThat(first.getBody()).containsKey("id");

        ResponseEntity<Map<String, Object>> second =
                rest.exchange(base + "/api/users", HttpMethod.POST, create,
                        new ParameterizedTypeReference<>() {});

        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(second.getBody()).isNotNull();
        assertThat(second.getBody()).containsKey("type");
        assertThat(second.getBody()).containsKey("status");
    }


    @Test
    void getByEmail_returnsUser() {
        String base = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> create = new HttpEntity<>(
                "{\"email\":\"lookup@example.com\",\"fullName\":\"Lookup User\"}",
                headers
        );

        ResponseEntity<Map<String, Object>> created =
                rest.exchange(base + "/api/users", HttpMethod.POST, create,
                        new ParameterizedTypeReference<>() {});

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Map<String, Object>> fetched =
                rest.exchange(base + "/api/users/by-email?email=lookup@example.com", HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {});

        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody()).isNotNull();
        assertThat(fetched.getBody()).containsEntry("email", "lookup@example.com");
        assertThat(fetched.getBody()).containsEntry("fullName", "Lookup User");
    }

    @Test
    void deleteUser_thenNotFoundOnGet() {
        String base = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> create = new HttpEntity<>(
                "{\"email\":\"delete@example.com\",\"fullName\":\"Delete User\"}",
                headers
        );

        ResponseEntity<Map<String, Object>> created =
                rest.exchange(base + "/api/users", HttpMethod.POST, create,
                        new ParameterizedTypeReference<>() {});

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();

        String id = String.valueOf(created.getBody().get("id"));
        assertThat(id).isNotBlank();

        ResponseEntity<Void> deleted =
                rest.exchange(base + "/api/users/" + id, HttpMethod.DELETE, null, Void.class);

        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Map<String, Object>> getAfterDelete =
                rest.exchange(base + "/api/users/" + id, HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {});

        assertThat(getAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getAfterDelete.getBody()).isNotNull();
        assertThat(getAfterDelete.getBody()).containsKey("type");
        assertThat(getAfterDelete.getBody()).containsEntry("status", 404);
    }



}
