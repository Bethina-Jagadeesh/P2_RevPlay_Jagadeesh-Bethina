package com.rev.app.service;

import com.rev.app.dto.UserAccountRequestDto;
import com.rev.app.dto.UserAccountResponseDto;
import com.rev.app.entity.UserAccount;
import com.rev.app.mapper.UserAccountMapper;
import com.rev.app.repository.IUserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserAccountServiceImplTest {

    @Mock
    private IUserAccountRepository userRepository;

    @Mock
    private UserAccountMapper userAccountMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        UserAccountRequestDto request = new UserAccountRequestDto();
        request.setEmail("test@email.com");
        request.setPassword("password123");
        request.setSecurityAnswer("answer");

        UserAccount userEntity = new UserAccount();
        userEntity.setEmail(request.getEmail());

        UserAccountResponseDto responseDto = new UserAccountResponseDto();
        responseDto.setEmail(request.getEmail());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userAccountMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_value");
        when(userRepository.save(any(UserAccount.class))).thenReturn(userEntity);
        when(userAccountMapper.toResponseDto(userEntity)).thenReturn(responseDto);

        // Act
        UserAccountResponseDto result = userService.createUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(UserAccount.class));
    }

    @Test
    void testCreateUser_DuplicateEmail() {
        // Arrange
        UserAccountRequestDto request = new UserAccountRequestDto();
        request.setEmail("duplicate@email.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new UserAccount()));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(request));
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserAccount.class));
    }

    @Test
    void testGetUserById() {
        // Arrange
        int userId = 1;
        UserAccount user = new UserAccount();
        user.setUserId(userId);
        UserAccountResponseDto responseDto = new UserAccountResponseDto();
        responseDto.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userAccountMapper.toResponseDto(user)).thenReturn(responseDto);

        // Act
        UserAccountResponseDto result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }
}
