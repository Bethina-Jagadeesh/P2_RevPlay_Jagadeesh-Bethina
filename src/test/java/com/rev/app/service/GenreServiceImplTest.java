package com.rev.app.service;

import com.rev.app.dto.GenreRequestDto;
import com.rev.app.dto.GenreResponseDto;
import com.rev.app.entity.Genre;
import com.rev.app.mapper.GenreMapper;
import com.rev.app.repository.IGenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    @Mock
    private IGenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGenre() {
        GenreRequestDto request = new GenreRequestDto();
        when(genreMapper.toEntity(any())).thenReturn(new Genre());
        when(genreRepository.save(any())).thenReturn(new Genre());
        when(genreMapper.toResponseDto(any())).thenReturn(new GenreResponseDto());

        genreService.createGenre(request);

        verify(genreRepository).save(any());
    }

    @Test
    void testGetGenreById() {
        when(genreRepository.findById(1)).thenReturn(Optional.of(new Genre()));
        when(genreMapper.toResponseDto(any())).thenReturn(new GenreResponseDto());

        genreService.getGenreById(1);

        verify(genreRepository).findById(1);
    }
}
