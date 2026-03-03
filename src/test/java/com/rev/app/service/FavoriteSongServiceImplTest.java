package com.rev.app.service;

import com.rev.app.dto.FavoriteSongRequestDto;
import com.rev.app.dto.FavoriteSongResponseDto;
import com.rev.app.entity.FavoriteSong;
import com.rev.app.mapper.FavoriteSongMapper;
import com.rev.app.repository.IFavoriteSongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FavoriteSongServiceImplTest {

    @Mock
    private IFavoriteSongRepository favoriteSongRepository;

    @Mock
    private FavoriteSongMapper favoriteSongMapper;

    @InjectMocks
    private FavoriteSongServiceImpl favoriteSongService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddFavorite() {
        FavoriteSongRequestDto request = new FavoriteSongRequestDto();
        request.setUserId(1);
        request.setSongId(101);

        when(favoriteSongRepository.existsByUserIdAndSongId(1, 101)).thenReturn(false);
        when(favoriteSongMapper.toEntity(any())).thenReturn(new FavoriteSong());
        when(favoriteSongRepository.save(any())).thenReturn(new FavoriteSong());
        when(favoriteSongMapper.toResponseDto(any())).thenReturn(new FavoriteSongResponseDto());

        favoriteSongService.addFavorite(request);

        verify(favoriteSongRepository).save(any());
    }

    @Test
    void testToggleFavorite_Remove() {
        when(favoriteSongRepository.existsByUserIdAndSongId(1, 101)).thenReturn(true);

        boolean result = favoriteSongService.toggleFavorite(1, 101);

        assertFalse(result);
        verify(favoriteSongRepository).deleteByUserIdAndSongId(1, 101);
    }

    @Test
    void testToggleFavorite_Add() {
        when(favoriteSongRepository.existsByUserIdAndSongId(1, 101)).thenReturn(false);

        boolean result = favoriteSongService.toggleFavorite(1, 101);

        assertTrue(result);
        verify(favoriteSongRepository).save(any());
    }
}
