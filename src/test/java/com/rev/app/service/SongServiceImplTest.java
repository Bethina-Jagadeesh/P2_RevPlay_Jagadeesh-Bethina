package com.rev.app.service;

import com.rev.app.dto.SongRequestDto;
import com.rev.app.dto.SongResponseDto;
import com.rev.app.entity.Song;
import com.rev.app.mapper.SongMapper;
import com.rev.app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SongServiceImplTest {

    @Mock
    private ISongRepository songRepository;
    @Mock
    private IAlbumRepository albumRepository;
    @Mock
    private IArtistAccountRepository artistRepository;
    @Mock
    private IGenreRepository genreRepository;
    @Mock
    private SongMapper songMapper;
    @Mock
    private IFavoriteSongRepository favoriteSongRepository;
    @Mock
    private IListeningHistoryRepository listeningHistoryRepository;
    @Mock
    private IPlaylistSongRepository playlistSongRepository;

    @InjectMocks
    private SongServiceImpl songService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSong() {
        SongRequestDto request = new SongRequestDto();
        when(songMapper.toEntity(any())).thenReturn(new Song());
        when(songRepository.save(any())).thenReturn(new Song());
        when(songMapper.toResponseDto(any())).thenReturn(new SongResponseDto());

        songService.createSong(request);

        verify(songRepository).save(any());
    }

    @Test
    void testGetSongById() {
        Song song = new Song();
        song.setSongId(1);
        when(songRepository.findById(1)).thenReturn(Optional.of(song));
        when(songMapper.toResponseDto(any())).thenReturn(new SongResponseDto());

        songService.getSongById(1);

        verify(songRepository).findById(1);
    }

    @Test
    void testIncrementPlayCount() {
        Song song = new Song();
        song.setPlayCount(5);
        when(songRepository.findById(1)).thenReturn(Optional.of(song));

        songService.incrementPlayCount(1);

        assertEquals(6, song.getPlayCount());
        verify(songRepository).save(song);
    }
}
