package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PodcastEpisodeResponseDto {
    private int episodeId;
    private int podcastId;
    private String title;
    private int durationSeconds;
    private LocalDate releaseDate;
    private String fileUrl;
    private int playCount;
    private LocalDateTime createdAt;
}
