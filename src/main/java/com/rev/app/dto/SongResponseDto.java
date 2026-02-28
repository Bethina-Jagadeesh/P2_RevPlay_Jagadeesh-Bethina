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
public class SongResponseDto {
    private int songId;
    private int artistId;
    private Integer albumId;
    private int genreId;
    private String title;
    private int durationSeconds;
    private LocalDate releaseDate;
    private String fileUrl;
    private int playCount;
    private String isActive;
    private LocalDateTime createdAt;
    private String genreName;
    private String albumName;
    private String artistName;
    private String coverImageUrl;
    private boolean isFavorite;
}
