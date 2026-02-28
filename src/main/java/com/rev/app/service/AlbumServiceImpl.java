package com.rev.app.service;

import com.rev.app.dto.AlbumRequestDto;
import com.rev.app.dto.AlbumResponseDto;
import com.rev.app.entity.Album;
import com.rev.app.mapper.IAlbumMapper;
import com.rev.app.repository.IAlbumRepository;
import org.springframework.stereotype.Service;
import com.rev.app.service.IAlbumService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumServiceImpl implements IAlbumService {

    private final IAlbumRepository albumRepository;

    public AlbumServiceImpl(IAlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public AlbumResponseDto createAlbum(AlbumRequestDto requestDto) {
        Album album = IAlbumMapper.toEntity(requestDto);
        album = albumRepository.save(album);
        return IAlbumMapper.toResponseDto(album);
    }

    @Override
    public AlbumResponseDto getAlbumById(int id) {
        return albumRepository.findById(id)
                .map(IAlbumMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<AlbumResponseDto> getAllAlbums() {
        return albumRepository.findAll().stream()
                .map(IAlbumMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlbumResponseDto> getAlbumsByArtistId(int artistId) {
        return albumRepository.findByArtistId(artistId).stream()
                .map(IAlbumMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public AlbumResponseDto updateAlbum(int id, AlbumRequestDto requestDto) {
        Album existingAlbum = albumRepository.findById(id).orElse(null);
        if (existingAlbum != null) {
            existingAlbum.setTitle(requestDto.getTitle());
            existingAlbum.setDescription(requestDto.getDescription());
            if (requestDto.getCoverImageUrl() != null)
                existingAlbum.setCoverImageUrl(requestDto.getCoverImageUrl());
            if (requestDto.getReleaseDate() != null)
                existingAlbum.setReleaseDate(requestDto.getReleaseDate());
            albumRepository.save(existingAlbum);
            return IAlbumMapper.toResponseDto(existingAlbum);
        }
        return null;
    }

    @Override
    public void deleteAlbum(int id) {
        albumRepository.deleteById(id);
    }
}
