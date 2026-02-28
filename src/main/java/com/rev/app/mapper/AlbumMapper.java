package com.rev.app.mapper;

import com.rev.app.dto.AlbumRequestDto;
import com.rev.app.dto.AlbumResponseDto;
import com.rev.app.entity.Album;
import org.springframework.stereotype.Component;

@Component
public class AlbumMapper {

    public Album toEntity(AlbumRequestDto dto) {
        if (dto == null)
            return null;
        Album entity = new Album();
        entity.setArtistId(dto.getArtistId());
        entity.setTitle(dto.getTitle());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setDescription(dto.getDescription());
        entity.setCoverImageUrl(dto.getCoverImageUrl());
        return entity;
    }

    public AlbumResponseDto toResponseDto(Album entity) {
        if (entity == null)
            return null;
        return AlbumResponseDto.builder()
                .albumId(entity.getAlbumId())
                .artistId(entity.getArtistId())
                .title(entity.getTitle())
                .releaseDate(entity.getReleaseDate())
                .description(entity.getDescription())
                .coverImageUrl(entity.getCoverImageUrl())
                .createdAt(entity.getCreatedAt())
                .artistName(entity.getArtistName())
                .build();
    }
}
