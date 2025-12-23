package com.nexia.core.api;

import com.nexia.core.api.dto.CreateUserRequest;
import com.nexia.core.api.dto.UserResponse;
import com.nexia.core.domain.User;
import com.nexia.core.repo.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest req) {
        if (users.existsByEmail(req.email())) {
            throw new com.nexia.core.api.error.ConflictException("email already exists");
        }
        User u = new User(UUID.randomUUID(), req.email().trim(), req.fullName().trim(), Instant.now());
        User saved = users.save(u);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getFullName(), saved.getCreatedAt());
    }

    @GetMapping
    public List<UserResponse> list() {
        return users.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getCreatedAt()))
                .toList();
    }
}
