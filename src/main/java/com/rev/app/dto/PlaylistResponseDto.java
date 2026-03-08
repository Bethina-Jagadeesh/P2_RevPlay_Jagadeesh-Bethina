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
public class PlaylistResponseDto {
    private int playlistId;
    private int userId;
    private String name;
    private String description;
    private String privacyStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
