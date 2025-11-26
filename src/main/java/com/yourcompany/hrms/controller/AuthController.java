package com.yourcompany.hrms.controller;

import com.yourcompany.hrms.auth.AuthenticationRequest;
import com.yourcompany.hrms.auth.AuthenticationResponse;
import com.yourcompany.hrms.config.ResponseWrapper;
import com.yourcompany.hrms.entity.user.User;
import com.yourcompany.hrms.entity.user.UserResponse;
import com.yourcompany.hrms.jwt.JwtService;
import com.yourcompany.hrms.repository.UserRepository;
import com.yourcompany.hrms.service.UserDetailsServiceImpl;
import com.yourcompany.hrms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;

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
       // UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User user = userRepository.findByEmailWithAllRelations(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));



        // Generate JWT token
        String token = jwtService.generateToken(user);

        UserResponse userResponse = userService.toUserResponse(user);

        // Return authentication response with token
//        AuthenticationResponse authResponse = AuthenticationResponse.builder()
//                .token(token)
//                .build();

        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .token(token)
                .userData(userResponse)
                .build();

        return ResponseEntity.ok(ResponseWrapper.success("Login successful", authResponse));
    }


}

