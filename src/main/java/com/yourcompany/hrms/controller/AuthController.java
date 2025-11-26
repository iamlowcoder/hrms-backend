package com.yourcompany.hrms.controller;

import com.yourcompany.hrms.auth.AuthenticationRequest;
import com.yourcompany.hrms.auth.AuthenticationResponse;
import com.yourcompany.hrms.config.ResponseWrapper;
import com.yourcompany.hrms.jwt.JwtService;
import com.yourcompany.hrms.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> login(@Valid @RequestBody AuthenticationRequest request) {
        // Authenticate user credentials using AuthenticationManager
        // This will use BCryptPasswordEncoder automatically via AuthenticationProvider
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Load UserDetails after successful authentication
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Generate JWT token
        String token = jwtService.generateToken(userDetails);

        // Return authentication response with token
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .token(token)
                .build();

        return ResponseEntity.ok(ResponseWrapper.success("Login successful", authResponse));
    }
}

