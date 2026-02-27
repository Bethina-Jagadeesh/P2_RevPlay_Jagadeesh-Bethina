package com.rev.app.mapper;

import com.rev.app.dto.ArtistAccountRequestDto;
import com.rev.app.dto.ArtistAccountResponseDto;
import com.rev.app.entity.ArtistAccount;
import org.springframework.stereotype.Component;

@Component
public class ArtistAccountMapper {

    public ArtistAccount toEntity(ArtistAccountRequestDto dto) {
        if (dto == null)

            return null;
        ArtistAccount entity = new ArtistAccount();
        entity.setStageName(dto.getStageName());
        entity.setEmail(dto.getEmail());
        entity.setPasswordHash(dto.getPassword()); // Hash in real app
        entity.setBio(dto.getBio());
        entity.setGenre(dto.getGenre());
        entity.setInstagramLink(dto.getInstagramLink());
        entity.setYoutubeLink(dto.getYoutubeLink());
        entity.setSpotifyLink(dto.getSpotifyLink());
        entity.setStatus("ACTIVE"); // Default
        entity.setProfileImageUrl(dto.getProfileImageUrl());
        return entity;
    }

    public ArtistAccountResponseDto toResponseDto(ArtistAccount entity) {
        if (entity == null)
            return null;
        return ArtistAccountResponseDto.builder()
                .artistId(entity.getArtistId())
                .stageName(entity.getStageName())
                .email(entity.getEmail())
                .bio(entity.getBio())
                .genre(entity.getGenre())
                .instagramLink(entity.getInstagramLink())
                .youtubeLink(entity.getYoutubeLink())
                .spotifyLink(entity.getSpotifyLink())
                .status(entity.getStatus())
                .profileImageUrl(entity.getProfileImageUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
