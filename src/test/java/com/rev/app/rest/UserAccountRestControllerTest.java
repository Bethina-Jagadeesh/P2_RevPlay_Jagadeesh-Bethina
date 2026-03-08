package com.rev.app.rest;

import com.rev.app.dto.UserAccountResponseDto;
import com.rev.app.service.IUserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserAccountRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IUserAccountService userService;

    @InjectMocks
    private UserAccountRestController userRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userRestController).build();
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserAccountResponseDto user = new UserAccountResponseDto();
        user.setUserId(1);
        user.setFullName("Test User");
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Test User"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }
}
