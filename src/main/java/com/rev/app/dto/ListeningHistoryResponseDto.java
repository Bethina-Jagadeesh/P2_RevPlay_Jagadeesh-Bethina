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
public class ListeningHistoryResponseDto {
    private int historyId;
    private int userId;
    private int songId;
    private LocalDateTime playedAt;
    private String actionType;
}
