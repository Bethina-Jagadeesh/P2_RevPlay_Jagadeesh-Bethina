package com.rev.app.mapper;

import com.rev.app.dto.FavoriteSongRequestDto;
import com.rev.app.dto.FavoriteSongResponseDto;
import com.rev.app.entity.FavoriteSong;
import org.springframework.stereotype.Component;

@Component
public class FavoriteSongMapper {

    public FavoriteSong toEntity(FavoriteSongRequestDto dto) {
        if (dto == null)
            return null;
        FavoriteSong entity = new FavoriteSong();
        entity.setUserId(dto.getUserId());
        entity.setSongId(dto.getSongId());
        return entity;
    }

    public FavoriteSongResponseDto toResponseDto(FavoriteSong entity) {
        if (entity == null)
            return null;
        return FavoriteSongResponseDto.builder()
                .userId(entity.getUserId())
                .songId(entity.getSongId())
                .favoritedAt(entity.getFavoritedAt())
                .build();
    }
}
