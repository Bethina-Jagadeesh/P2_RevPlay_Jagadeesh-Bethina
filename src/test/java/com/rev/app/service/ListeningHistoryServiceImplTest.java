package com.rev.app.service;

import com.rev.app.dto.ListeningHistoryRequestDto;
import com.rev.app.dto.ListeningHistoryResponseDto;
import com.rev.app.entity.ListeningHistory;
import com.rev.app.mapper.ListeningHistoryMapper;
import com.rev.app.repository.IListeningHistoryRepository;
import com.rev.app.repository.ISongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ListeningHistoryServiceImplTest {

    @Mock
    private IListeningHistoryRepository historyRepository;

    @Mock
    private ListeningHistoryMapper listeningHistoryMapper;

    @Mock
    private ISongRepository songRepository;

    @InjectMocks
    private ListeningHistoryServiceImpl historyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRecordHistory() {
        ListeningHistoryRequestDto request = new ListeningHistoryRequestDto();
        when(listeningHistoryMapper.toEntity(any())).thenReturn(new ListeningHistory());
        when(historyRepository.save(any())).thenReturn(new ListeningHistory());
        when(listeningHistoryMapper.toResponseDto(any())).thenReturn(new ListeningHistoryResponseDto());

        historyService.recordHistory(request);

        verify(historyRepository).save(any());
    }

    @Test
    void testClearHistory() {
        historyService.clearHistory(1);
        verify(historyRepository).deleteByUserId(1);
    }
}
