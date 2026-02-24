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
public class PodcastResponseDto {
    private int podcastId;
    private String title;
    private String hostName;
    private String category;
    private String description;
    private LocalDateTime createdAt;
}
