package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistAccountResponseDto {
    private int artistId;
    private String stageName;
    private String email;
    private String bio;
    private String genre;
    private String instagramLink;
    private String youtubeLink;
    private String spotifyLink;
    private String status;
    private String profileImageUrl;
    private LocalDateTime createdAt;
}
