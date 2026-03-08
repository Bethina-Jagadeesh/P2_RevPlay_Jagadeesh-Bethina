package com.rev.app.service;

import com.rev.app.dto.PlaylistRequestDto;
import com.rev.app.dto.PlaylistResponseDto;
import com.rev.app.entity.Playlist;
import com.rev.app.entity.PlaylistSong;
import com.rev.app.mapper.PlaylistMapper;
import com.rev.app.repository.IPlaylistRepository;
import com.rev.app.repository.IPlaylistSongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlaylistServiceImplTest {

    @Mock
    private IPlaylistRepository playlistRepository;

    @Mock
    private IPlaylistSongRepository playlistSongRepository;

    @Mock
    private PlaylistMapper playlistMapper;

    @InjectMocks
    private PlaylistServiceImpl playlistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePlaylist() {
        PlaylistRequestDto request = new PlaylistRequestDto();
        when(playlistMapper.toEntity(any())).thenReturn(new Playlist());
        when(playlistRepository.save(any())).thenReturn(new Playlist());
        when(playlistMapper.toResponseDto(any())).thenReturn(new PlaylistResponseDto());

        playlistService.createPlaylist(request);

        verify(playlistRepository).save(any());
    }

    @Test
    void testAddSongToPlaylist() {
        playlistService.addSongToPlaylist(1, 101);
        verify(playlistSongRepository).save(any(PlaylistSong.class));
    }

    @Test
    void testRemoveSongFromPlaylist() {
        playlistService.removeSongFromPlaylist(1, 101);
        verify(playlistSongRepository).deleteByPlaylistIdAndSongId(1, 101);
    }
}
