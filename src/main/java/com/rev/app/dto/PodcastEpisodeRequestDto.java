package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PodcastEpisodeRequestDto {
    private int podcastId;
    private String title;
    private int durationSeconds;
    private LocalDate releaseDate;
    private String fileUrl;
}
