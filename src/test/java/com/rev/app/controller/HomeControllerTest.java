package com.rev.app.controller;

import com.rev.app.dto.UserAccountRequestDto;
import com.rev.app.dto.UserAccountResponseDto;
import com.rev.app.repository.IArtistAccountRepository;
import com.rev.app.repository.IUserAccountRepository;
import com.rev.app.service.IArtistAccountService;
import com.rev.app.service.IUserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

class HomeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IUserAccountService userService;

    @Mock
    private IArtistAccountService artistService;

    @Mock
    private IUserAccountRepository userRepository;

    @Mock
    private IArtistAccountRepository artistRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @Test
    void testProcessRegisterListener_Success() throws Exception {
        // Arrange
        UserAccountRequestDto requestDto = new UserAccountRequestDto();
        requestDto.setEmail("new@user.com");

        when(userService.createUser(any(UserAccountRequestDto.class))).thenReturn(new UserAccountResponseDto());

        // Act & Assert
        mockMvc.perform(post("/register/listener")
                .param("email", "new@user.com")
                .param("fullName", "Test User"))
                .andExpect(redirectedUrl("/?registered=true"));

        verify(userService, times(1)).createUser(any(UserAccountRequestDto.class));
    }

    @Test
    void testProcessRegisterListener_Failure() throws Exception {
        // Arrange
        when(userService.createUser(any(UserAccountRequestDto.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/register/listener")
                .param("email", "duplicate@user.com"))
                .andExpect(view().name("listener/register"))
                .andExpect(model().attributeExists("error"));

        verify(userService, times(1)).createUser(any(UserAccountRequestDto.class));
    }
}
