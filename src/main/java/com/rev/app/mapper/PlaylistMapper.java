package com.rev.app.mapper;

import com.rev.app.dto.PlaylistRequestDto;
import com.rev.app.dto.PlaylistResponseDto;
import com.rev.app.entity.Playlist;
import org.springframework.stereotype.Component;

@Component
public class PlaylistMapper {

    public Playlist toEntity(PlaylistRequestDto dto) {
        if (dto == null)
            return null;
        Playlist entity = new Playlist();
        entity.setUserId(dto.getUserId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrivacyStatus(dto.getPrivacyStatus() != null ? dto.getPrivacyStatus() : "PUBLIC");
        return entity;
    }

    public PlaylistResponseDto toResponseDto(Playlist entity) {
        if (entity == null)
            return null;
        return PlaylistResponseDto.builder()
                .playlistId(entity.getPlaylistId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .description(entity.getDescription())
                .privacyStatus(entity.getPrivacyStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
