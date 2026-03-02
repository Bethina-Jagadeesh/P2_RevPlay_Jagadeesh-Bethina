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
public class AlbumResponseDto {
    private int albumId;
    private int artistId;
    private String title;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private LocalDate releaseDate;

    private String description;
    private String coverImageUrl;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private LocalDateTime createdAt;

    private String artistName;
}
