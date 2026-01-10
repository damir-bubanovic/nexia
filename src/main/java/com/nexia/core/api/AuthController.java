package com.nexia.core.api;

import com.nexia.core.api.dto.AuthResponse;
import com.nexia.core.api.dto.LoginRequest;
import com.nexia.core.api.dto.RegisterRequest;
import com.nexia.core.api.error.ConflictException;
import com.nexia.core.domain.User;
import com.nexia.core.messaging.UserEventPublisher;
import com.nexia.core.messaging.events.UserRegisteredEvent;
import com.nexia.core.repo.UserRepository;
import com.nexia.core.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserEventPublisher userEventPublisher;

    public AuthController(UserRepository users,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserEventPublisher userEventPublisher) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userEventPublisher = userEventPublisher;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        String email = request.email().trim();

        if (users.existsByEmail(email)) {
            throw new ConflictException("email already exists");
        }

        User user = new User(
                UUID.randomUUID(),
                email,
                request.fullName().trim(),
                Instant.now(),
                passwordEncoder.encode(request.password()),
                "USER"
        );

        User saved = users.save(user);

        // Phase 6: publish async event
        userEventPublisher.publishUserRegistered(
                UserRegisteredEvent.of(saved.getId(), saved.getEmail())
        );

        String token = jwtService.generate(saved);
        return new AuthResponse(token, "Bearer", jwtService.ttlSeconds());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        String email = request.email().trim();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password())
            );
        } catch (Exception ex) {
            throw new BadCredentialsException("invalid credentials");
        }

        User user = users.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("invalid credentials"));

        String token = jwtService.generate(user);
        return new AuthResponse(token, "Bearer", jwtService.ttlSeconds());
    }
}
