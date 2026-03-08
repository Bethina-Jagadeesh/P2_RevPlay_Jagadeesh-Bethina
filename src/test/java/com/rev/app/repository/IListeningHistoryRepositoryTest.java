package com.rev.app.repository;

import com.rev.app.entity.ListeningHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class IListeningHistoryRepositoryTest {

    @Autowired
    private IListeningHistoryRepository listeningHistoryRepository;

    @Test
    void testFindByUserIdOrderByPlayedAtDesc() {
        ListeningHistory h1 = ListeningHistory.builder()
                .userId(1)
                .songId(101)
                .playedAt(LocalDateTime.now().minusHours(1))
                .build();
        ListeningHistory h2 = ListeningHistory.builder()
                .userId(1)
                .songId(102)
                .playedAt(LocalDateTime.now())
                .build();
        listeningHistoryRepository.save(h1);
        listeningHistoryRepository.save(h2);

        List<ListeningHistory> history = listeningHistoryRepository.findByUserIdOrderByPlayedAtDesc(1);
        assertEquals(2, history.size());
        assertEquals(102, history.get(0).getSongId()); // Most recent first
    }

    @Test
    void testDeleteByUserId() {
        ListeningHistory h = ListeningHistory.builder().userId(1).songId(101).playedAt(LocalDateTime.now()).build();
        listeningHistoryRepository.save(h);

        listeningHistoryRepository.deleteByUserId(1);
        List<ListeningHistory> history = listeningHistoryRepository.findByUserIdOrderByPlayedAtDesc(1);
        assertTrue(history.isEmpty());
    }
}
