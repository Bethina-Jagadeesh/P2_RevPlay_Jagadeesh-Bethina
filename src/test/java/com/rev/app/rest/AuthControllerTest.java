package com.rev.app.rest;

import com.rev.app.dto.*;
import com.rev.app.security.JwtUtil;
import com.rev.app.service.IArtistAccountService;
import com.rev.app.service.IUserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private IUserAccountService userService;
    @Mock
    private IArtistAccountService artistService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegisterUser() throws Exception {
        when(userService.createUser(any())).thenReturn(new UserAccountResponseDto());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\", \"password\":\"pass\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegisterArtist() throws Exception {
        when(artistService.createArtist(any())).thenReturn(new ArtistAccountResponseDto());

        mockMvc.perform(post("/api/auth/register/artist")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"artist@test.com\", \"password\":\"pass\"}"))
                .andExpect(status().isCreated());
    }
}
