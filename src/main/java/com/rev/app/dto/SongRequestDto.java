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
public class SongRequestDto {
    private int artistId;
    private Integer albumId;
    private int genreId;
    private String title;
    private int durationSeconds;
    private LocalDate releaseDate;
    private String fileUrl;
}
