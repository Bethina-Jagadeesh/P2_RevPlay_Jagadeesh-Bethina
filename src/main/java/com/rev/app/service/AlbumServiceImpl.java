package com.rev.app.service;

import com.rev.app.dto.AlbumRequestDto;
import com.rev.app.dto.AlbumResponseDto;
import com.rev.app.entity.Album;
import com.rev.app.mapper.AlbumMapper;
import com.rev.app.repository.IAlbumRepository;
import com.rev.app.repository.IArtistAccountRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumServiceImpl implements IAlbumService {

    private final IAlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final IArtistAccountRepository artistAccountRepository;

    public AlbumServiceImpl(IAlbumRepository albumRepository, AlbumMapper albumMapper,
                            IArtistAccountRepository artistAccountRepository) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
        this.artistAccountRepository = artistAccountRepository;
    }

    private Album populateArtistName(Album album) {
        if (album != null) {
            artistAccountRepository.findById(album.getArtistId()).ifPresent(artist -> {
                album.setArtistName(artist.getStageName());
            });
        }
        return album;
    }

    @Override
    public AlbumResponseDto createAlbum(AlbumRequestDto requestDto) {
        Album album = albumMapper.toEntity(requestDto);
        album = albumRepository.save(album);
        return albumMapper.toResponseDto(populateArtistName(album));
    }

    @Override
    public AlbumResponseDto getAlbumById(int id) {
        return albumRepository.findById(id)
                .map(this::populateArtistName)
                .map(albumMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    public List<AlbumResponseDto> getAllAlbums() {
        return albumRepository.findAll().stream()
                .map(this::populateArtistName)
                .map(albumMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlbumResponseDto> getAlbumsByArtistId(int artistId) {
        return albumRepository.findByArtistId(artistId).stream()
                .map(this::populateArtistName)
                .map(albumMapper::toResponseDto)
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
            return albumMapper.toResponseDto(populateArtistName(existingAlbum));
        }
        return null;
    }

    @Override
    public void deleteAlbum(int id) {
        albumRepository.deleteById(id);
    }
}
