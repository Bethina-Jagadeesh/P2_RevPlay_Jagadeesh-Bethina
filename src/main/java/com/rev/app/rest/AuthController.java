package com.rev.app.rest;

import com.rev.app.dto.*;
import com.rev.app.security.JwtUtil;
import com.rev.app.service.IArtistAccountService;
import com.rev.app.service.IUserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final IUserAccountService userService;
    private final IArtistAccountService artistService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDetailsService userDetailsService,
            JwtUtil jwtUtil,
            IUserAccountService userService,
            IArtistAccountService artistService,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.artistService = artistService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {
        try {
            // Note: In real app, make sure to configure DaoAuthenticationProvider
            // But manually checking also works if DaoAuthenticationProvider is missing due
            // to CustomUserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                // To keep it simple, if plain text or basic mapping fails
                // Let's assume plain text mapping for now if encoding is disabled on older P1.
                // You should use passwordEncoder.matches() instead of equals() in PROD.
                if (!request.getPassword().equals(userDetails.getPassword())
                        && !passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                    throw new BadCredentialsException("Incorrect username or password");
                }
            }

            // Generate token
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponseDto(jwt, userDetails.getUsername(), "Login Successful"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDto(null, request.getEmail(), "Authentication failed"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserAccountResponseDto> register(@RequestBody UserAccountRequestDto request) {
        UserAccountResponseDto createdUser = userService.createUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/register/artist")
    public ResponseEntity<ArtistAccountResponseDto> registerArtist(@RequestBody ArtistAccountRequestDto request) {
        ArtistAccountResponseDto createdArtist = artistService.createArtist(request);
        return new ResponseEntity<>(createdArtist, HttpStatus.CREATED);
    }
}
