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
public class AlbumRequestDto {
    private int artistId;
    private String title;
    private LocalDate releaseDate;
    private String description;
    private String coverImageUrl;
}
