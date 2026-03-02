package com.rev.app.mapper;

import com.rev.app.dto.PodcastRequestDto;
import com.rev.app.dto.PodcastResponseDto;
import com.rev.app.entity.Podcast;
import com.rev.app.entity.ArtistAccount;
import com.rev.app.repository.IArtistAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PodcastMapper {

    @Autowired
    private IArtistAccountRepository artistRepository;

    public Podcast toEntity(PodcastRequestDto dto) {
        if (dto == null)
            return null;
        Podcast entity = new Podcast();
        entity.setTitle(dto.getTitle());
        entity.setHostName(dto.getHostName());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setFileUrl(dto.getFileUrl());
        entity.setArtistId(dto.getArtistId());
        return entity;
    }

    public PodcastResponseDto toResponseDto(Podcast entity) {
        if (entity == null)
            return null;

        String coverImage = null;
        if (artistRepository != null) {
            java.util.Optional<ArtistAccount> opt = artistRepository.findById(entity.getArtistId());
            if (opt.isPresent() && opt.get().getProfileImageUrl() != null) {
                coverImage = opt.get().getProfileImageUrl();
            }
        }

        return PodcastResponseDto.builder()
                .podcastId(entity.getPodcastId())
                .title(entity.getTitle())
                .hostName(entity.getHostName())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .fileUrl(entity.getFileUrl())
                .artistId(entity.getArtistId())
                .coverImageUrl(coverImage)
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .build();
    }
}
