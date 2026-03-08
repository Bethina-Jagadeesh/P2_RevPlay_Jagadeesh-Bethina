package com.rev.app.mapper;

import com.rev.app.dto.ListeningHistoryRequestDto;
import com.rev.app.dto.ListeningHistoryResponseDto;
import com.rev.app.entity.ListeningHistory;
import org.springframework.stereotype.Component;

@Component
public class ListeningHistoryMapper {

    public ListeningHistory toEntity(ListeningHistoryRequestDto dto) {
        if (dto == null)
            return null;
        ListeningHistory entity = new ListeningHistory();
        entity.setUserId(dto.getUserId());
        entity.setSongId(dto.getSongId());
        entity.setActionType(dto.getActionType());
        return entity;
    }

    public ListeningHistoryResponseDto toResponseDto(ListeningHistory entity) {
        if (entity == null)
            return null;
        return ListeningHistoryResponseDto.builder()
                .historyId(entity.getHistoryId())
                .userId(entity.getUserId())
                .songId(entity.getSongId())
                .playedAt(entity.getPlayedAt())
                .actionType(entity.getActionType())
                .build();
    }
}
