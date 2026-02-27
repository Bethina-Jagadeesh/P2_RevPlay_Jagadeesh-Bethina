package com.rev.app.mapper;

import com.rev.app.dto.SongRequestDto;
import com.rev.app.dto.SongResponseDto;
import com.rev.app.entity.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

    public Song toEntity(SongRequestDto dto) {
        if (dto == null)
            return null;
        Song entity = new Song();
        entity.setArtistId(dto.getArtistId());
        entity.setAlbumId(dto.getAlbumId());
        entity.setGenreId(dto.getGenreId());
        entity.setTitle(dto.getTitle());
        entity.setDurationSeconds(dto.getDurationSeconds());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setFileUrl(dto.getFileUrl());
        entity.setIsActive("ACTIVE"); // Default
        return entity;
    }

    public SongResponseDto toResponseDto(Song entity) {
        if (entity == null)
            return null;
        return SongResponseDto.builder()
                .songId(entity.getSongId())
                .artistId(entity.getArtistId())
                .albumId(entity.getAlbumId())
                .genreId(entity.getGenreId())
                .title(entity.getTitle())
                .durationSeconds(entity.getDurationSeconds())
                .releaseDate(entity.getReleaseDate())
                .fileUrl(entity.getFileUrl())
                .playCount(entity.getPlayCount())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .genreName(entity.getGenreName())
                .albumName(entity.getAlbumName())
                .artistName(entity.getArtistName())
                .build();
    }
}
