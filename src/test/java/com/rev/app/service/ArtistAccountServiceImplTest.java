package com.rev.app.service;

import com.rev.app.dto.ArtistAccountRequestDto;
import com.rev.app.dto.ArtistAccountResponseDto;
import com.rev.app.entity.ArtistAccount;
import com.rev.app.mapper.ArtistAccountMapper;
import com.rev.app.repository.IArtistAccountRepository;
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

class ArtistAccountServiceImplTest {

    @Mock
    private IArtistAccountRepository artistRepository;

    @Mock
    private ArtistAccountMapper artistAccountMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ArtistAccountServiceImpl artistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateArtist_Success() {
        ArtistAccountRequestDto request = new ArtistAccountRequestDto();
        request.setEmail("test@artist.com");
        request.setPassword("password");

        when(artistRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(artistAccountMapper.toEntity(any())).thenReturn(new ArtistAccount());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(artistRepository.save(any())).thenReturn(new ArtistAccount());
        when(artistAccountMapper.toResponseDto(any())).thenReturn(new ArtistAccountResponseDto());

        artistService.createArtist(request);

        verify(artistRepository).save(any());
    }

    @Test
    void testCreateArtist_EmailExists() {
        ArtistAccountRequestDto request = new ArtistAccountRequestDto();
        request.setEmail("exists@artist.com");

        when(artistRepository.findByEmail("exists@artist.com")).thenReturn(Optional.of(new ArtistAccount()));

        assertThrows(RuntimeException.class, () -> artistService.createArtist(request));
    }
}
