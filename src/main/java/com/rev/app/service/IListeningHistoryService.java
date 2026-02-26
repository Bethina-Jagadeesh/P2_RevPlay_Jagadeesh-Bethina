package com.rev.app.service;

import com.rev.app.dto.ListeningHistoryRequestDto;
import com.rev.app.dto.ListeningHistoryResponseDto;

import java.util.List;

public interface IListeningHistoryService {
    ListeningHistoryResponseDto recordHistory(ListeningHistoryRequestDto requestDto);

    List<ListeningHistoryResponseDto> getHistoryByUser(int userId);

    void clearHistory(int userId);
}
