package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String fileUrl;
    private int artistId;
    private String coverImageUrl;
    private String createdAt;
}
