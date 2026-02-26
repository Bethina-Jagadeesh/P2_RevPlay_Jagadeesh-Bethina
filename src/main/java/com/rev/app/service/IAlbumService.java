package com.rev.app.service;

import com.rev.app.dto.AlbumRequestDto;
import com.rev.app.dto.AlbumResponseDto;

import java.util.List;

public interface IAlbumService {
    AlbumResponseDto createAlbum(AlbumRequestDto requestDto);

    AlbumResponseDto getAlbumById(int id);

    List<AlbumResponseDto> getAllAlbums();

    List<AlbumResponseDto> getAlbumsByArtistId(int artistId);

    AlbumResponseDto updateAlbum(int id, AlbumRequestDto requestDto);

    void deleteAlbum(int id);
}
