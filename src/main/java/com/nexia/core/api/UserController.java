package com.nexia.core.api;

import com.nexia.core.api.dto.CreateUserRequest;
import com.nexia.core.api.dto.UserResponse;
import com.nexia.core.api.error.ConflictException;
import com.nexia.core.api.error.NotFoundException;
import com.nexia.core.domain.User;
import com.nexia.core.repo.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @Operation(summary = "Create user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(@Valid @RequestBody CreateUserRequest req) {
        if (users.existsByEmail(req.email().trim())) {
            throw new ConflictException("email already exists");
        }
        // Uses the 4-arg constructor -> passwordHash null, role "USER"
        User u = new User(UUID.randomUUID(), req.email().trim(), req.fullName().trim(), Instant.now());
        User saved = users.save(u);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getFullName(), saved.getCreatedAt());
    }

    @Operation(summary = "List users (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> list(Pageable pageable) {
        int page = pageable.getPageNumber() < 0 ? 0 : pageable.getPageNumber();
        int size = pageable.getPageSize() <= 0 ? 20 : pageable.getPageSize();

        // Ignore client-provided sort completely; always sort by createdAt
        Pageable sanitized = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        return users.findAll(sanitized)
                .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getCreatedAt()));
    }

    @Operation(summary = "Get user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getById(@PathVariable UUID id) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("user not found"));
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getCreatedAt());
    }

    @Operation(summary = "Get current user (me)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserResponse me(Authentication authentication) {
        // DbUserDetailsService should use email as username; authentication.getName() returns that
        String email = authentication.getName();
        User u = users.findByEmail(email.trim()).orElseThrow(() -> new NotFoundException("user not found"));
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getCreatedAt());
    }

    @Operation(summary = "Get user by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/by-email")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getByEmail(@RequestParam String email) {
        User u = users.findByEmail(email.trim()).orElseThrow(() -> new NotFoundException("user not found"));
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getCreatedAt());
    }

    @Operation(summary = "Delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        if (!users.existsById(id)) {
            throw new NotFoundException("user not found");
        }
        users.deleteById(id);
    }
}
