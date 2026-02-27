package com.rev.app.mapper;

import com.rev.app.dto.PodcastEpisodeRequestDto;
import com.rev.app.dto.PodcastEpisodeResponseDto;
import com.rev.app.entity.PodcastEpisode;
import org.springframework.stereotype.Component;

@Component
public class PodcastEpisodeMapper {

    public PodcastEpisode toEntity(PodcastEpisodeRequestDto dto) {
        if (dto == null)
            return null;
        PodcastEpisode entity = new PodcastEpisode();
        entity.setPodcastId(dto.getPodcastId());
        entity.setTitle(dto.getTitle());
        entity.setDurationSeconds(dto.getDurationSeconds());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setFileUrl(dto.getFileUrl());
        return entity;
    }

    public PodcastEpisodeResponseDto toResponseDto(PodcastEpisode entity) {
        if (entity == null)
            return null;
        return PodcastEpisodeResponseDto.builder()
                .episodeId(entity.getEpisodeId())
                .podcastId(entity.getPodcastId())
                .title(entity.getTitle())
                .durationSeconds(entity.getDurationSeconds())
                .releaseDate(entity.getReleaseDate())
                .fileUrl(entity.getFileUrl())
                .playCount(entity.getPlayCount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
