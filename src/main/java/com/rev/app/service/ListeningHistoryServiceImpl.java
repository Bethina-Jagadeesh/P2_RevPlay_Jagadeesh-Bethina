package com.rev.app.service;

import com.rev.app.dto.ListeningHistoryRequestDto;
import com.rev.app.dto.ListeningHistoryResponseDto;
import com.rev.app.entity.ListeningHistory;
import com.rev.app.mapper.ListeningHistoryMapper;
import com.rev.app.repository.IListeningHistoryRepository;
import com.rev.app.repository.ISongRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import com.rev.app.repository.IUserAccountRepository;
import com.rev.app.dto.UserPlayCountDto;
import com.rev.app.dto.DailyTrendDto;
import com.rev.app.entity.UserAccount;
import com.rev.app.entity.Song;

@Service
public class ListeningHistoryServiceImpl implements IListeningHistoryService {

    private final IListeningHistoryRepository historyRepository;
    private final ListeningHistoryMapper listeningHistoryMapper;
    private final ISongRepository songRepository;
    private final IUserAccountRepository userRepository;

    public ListeningHistoryServiceImpl(IListeningHistoryRepository historyRepository,
            ListeningHistoryMapper listeningHistoryMapper,
            ISongRepository songRepository,
            IUserAccountRepository userRepository) {
        this.historyRepository = historyRepository;
        this.listeningHistoryMapper = listeningHistoryMapper;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
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

    @Override
    public Map<LocalDate, DailyTrendDto> getListeningTrends(List<Integer> songIds) {
        if (songIds == null || songIds.isEmpty())
            return new TreeMap<>();

        List<ListeningHistory> history = historyRepository.findBySongIdIn(songIds);

        Map<LocalDate, List<ListeningHistory>> groupedByDate = history.stream()
                .collect(Collectors.groupingBy(h -> h.getPlayedAt().toLocalDate()));

        Map<LocalDate, DailyTrendDto> result = new TreeMap<>();

        for (Map.Entry<LocalDate, List<ListeningHistory>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<ListeningHistory> dayHistory = entry.getValue();

            // Calculate total plays for the day
            long totalPlays = dayHistory.size();

            // Find the most played song for the day
            Map<Integer, Long> songCounts = dayHistory.stream()
                    .collect(Collectors.groupingBy(ListeningHistory::getSongId, Collectors.counting()));

            Integer topSongId = songCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            String topSongTitle = "N/A";
            if (topSongId != null) {
                topSongTitle = songRepository.findById(topSongId)
                        .map(Song::getTitle)
                        .orElse("Unknown Song");
            }

            result.put(date, DailyTrendDto.builder()
                    .totalPlays(totalPlays)
                    .topSongTitle(topSongTitle)
                    .build());
        }

        return result;
    }

    @Override
    public List<UserPlayCountDto> getTopListeners(List<Integer> songIds) {
        if (songIds == null || songIds.isEmpty())
            return List.of();
        Map<Integer, Long> userPlayCounts = historyRepository.findBySongIdIn(songIds).stream()
                .collect(Collectors.groupingBy(ListeningHistory::getUserId, Collectors.counting()));

        return userPlayCounts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .map(entry -> {
                    UserAccount user = userRepository.findById(entry.getKey()).orElse(null);
                    if (user == null)
                        return null;
                    return UserPlayCountDto.builder()
                            .userId(entry.getKey())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .profileImageUrl(user.getProfileImageUrl())
                            .playCount(entry.getValue())
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .limit(10)
                .collect(Collectors.toList());
    }
}
