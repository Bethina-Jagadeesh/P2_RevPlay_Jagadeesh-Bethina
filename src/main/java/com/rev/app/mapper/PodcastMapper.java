package com.rev.app.mapper;

import com.rev.app.dto.PodcastRequestDto;
import com.rev.app.dto.PodcastResponseDto;
import com.rev.app.entity.Podcast;
import org.springframework.stereotype.Component;

@Component
public class PodcastMapper {

    public Podcast toEntity(PodcastRequestDto dto) {
        if (dto == null)
            return null;
        Podcast entity = new Podcast();
        entity.setTitle(dto.getTitle());
        entity.setHostName(dto.getHostName());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setArtistId(dto.getArtistId());
        return entity;
    }

    public PodcastResponseDto toResponseDto(Podcast entity) {
        if (entity == null)
            return null;
        return PodcastResponseDto.builder()
                .podcastId(entity.getPodcastId())
                .title(entity.getTitle())
                .hostName(entity.getHostName())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .artistId(entity.getArtistId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
