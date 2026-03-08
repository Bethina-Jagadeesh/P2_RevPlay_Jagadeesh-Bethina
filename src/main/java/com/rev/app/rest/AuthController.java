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
import org.springframework.web.bind.annotation.*;

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
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                throw new BadCredentialsException("Incorrect username or password");
            }

            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponseDto(jwt, userDetails.getUsername(), "Login Successful"));

        } catch (Exception e) {
            System.err.println("Login failed for email: " + request.getEmail());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDto(null, request.getEmail(), "Authentication failed: " + e.getMessage()));
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
