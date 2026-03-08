package com.rev.app.service;

import com.rev.app.dto.AlbumRequestDto;
import com.rev.app.dto.AlbumResponseDto;
import com.rev.app.entity.Album;
import com.rev.app.entity.ArtistAccount;
import com.rev.app.mapper.AlbumMapper;
import com.rev.app.repository.IAlbumRepository;
import com.rev.app.repository.IArtistAccountRepository;
import com.rev.app.repository.ISongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AlbumServiceImplTest {

    @Mock
    private IAlbumRepository albumRepository;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private IArtistAccountRepository artistAccountRepository;

    @Mock
    private ISongRepository songRepository;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAlbum() {
        AlbumRequestDto request = new AlbumRequestDto();
        request.setArtistId(1);
        Album album = new Album();
        album.setArtistId(1);
        ArtistAccount artist = new ArtistAccount();
        artist.setStageName("Artist Name");

        when(albumMapper.toEntity(any())).thenReturn(album);
        when(albumRepository.save(any())).thenReturn(album);
        when(artistAccountRepository.findById(1)).thenReturn(Optional.of(artist));
        when(albumMapper.toResponseDto(any())).thenReturn(new AlbumResponseDto());

        albumService.createAlbum(request);

        verify(albumRepository).save(any());
    }

    @Test
    void testGetAlbumById() {
        Album album = new Album();
        album.setAlbumId(1);
        when(albumRepository.findById(1)).thenReturn(Optional.of(album));
        when(albumMapper.toResponseDto(any())).thenReturn(new AlbumResponseDto());

        albumService.getAlbumById(1);

        verify(albumRepository).findById(1);
    }
}
