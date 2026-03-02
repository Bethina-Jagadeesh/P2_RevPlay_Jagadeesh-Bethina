package com.rev.app.service;

import com.rev.app.dto.ListeningHistoryRequestDto;
import com.rev.app.dto.ListeningHistoryResponseDto;
import com.rev.app.entity.ListeningHistory;
import com.rev.app.mapper.ListeningHistoryMapper;
import com.rev.app.repository.IListeningHistoryRepository;
import com.rev.app.repository.ISongRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListeningHistoryServiceImpl implements IListeningHistoryService {

    private final IListeningHistoryRepository historyRepository;
    private final ListeningHistoryMapper listeningHistoryMapper;
    private final ISongRepository songRepository;

    public ListeningHistoryServiceImpl(IListeningHistoryRepository historyRepository,
            ListeningHistoryMapper listeningHistoryMapper,
            ISongRepository songRepository) {
        this.historyRepository = historyRepository;
        this.listeningHistoryMapper = listeningHistoryMapper;
        this.songRepository = songRepository;
    }

    @Override
    public ListeningHistoryResponseDto recordHistory(ListeningHistoryRequestDto requestDto) {
        ListeningHistory history = listeningHistoryMapper.toEntity(requestDto);
        history.setPlayedAt(LocalDateTime.now());
        history = historyRepository.save(history);
        return listeningHistoryMapper.toResponseDto(history);
    }

    @Override
    public List<ListeningHistoryResponseDto> getHistoryByUser(int userId) {
        return historyRepository.findByUserIdOrderByPlayedAtDesc(userId).stream()
                .map(history -> {
                    ListeningHistoryResponseDto dto = listeningHistoryMapper.toResponseDto(history);
                    songRepository.findById(history.getSongId())
                            .ifPresent(song -> dto.setSongTitle(song.getTitle()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void clearHistory(int userId) {
        historyRepository.deleteByUserId(userId);
    }
}
