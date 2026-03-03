package com.rev.app.service;

import com.rev.app.dto.ListeningHistoryRequestDto;
import com.rev.app.dto.ListeningHistoryResponseDto;
import java.util.List;

public interface IListeningHistoryService {
    ListeningHistoryResponseDto recordHistory(ListeningHistoryRequestDto requestDto);

    List<ListeningHistoryResponseDto> getHistoryByUser(int userId);

    void clearHistory(int userId);

    java.util.Map<java.time.LocalDate, com.rev.app.dto.DailyTrendDto> getListeningTrends(
            java.util.List<Integer> songIds);

    java.util.List<com.rev.app.dto.UserPlayCountDto> getTopListeners(java.util.List<Integer> songIds);

}
