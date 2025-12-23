package com.nexia.core.api;

import com.nexia.core.api.dto.CreateUserRequest;
import com.nexia.core.api.dto.UserResponse;
import com.nexia.core.api.error.ConflictException;
import com.nexia.core.api.error.NotFoundException;
import com.nexia.core.domain.User;
import com.nexia.core.repo.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
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
            throw new ConflictException("email already exists");
        }
        User u = new User(UUID.randomUUID(), req.email().trim(), req.fullName().trim(), Instant.now());
        User saved = users.save(u);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getFullName(), saved.getCreatedAt());
    }

    @GetMapping
    public Page<UserResponse> list(Pageable pageable) {
        return users.findAll(pageable)
                .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getCreatedAt()));
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("user not found"));
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getCreatedAt());
    }

    @GetMapping("/by-email")
    public UserResponse getByEmail(@RequestParam String email) {
        User u = users.findByEmail(email).orElseThrow(() -> new NotFoundException("user not found"));
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getCreatedAt());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        if (!users.existsById(id)) {
            throw new NotFoundException("user not found");
        }
        users.deleteById(id);
    }
}
