package com.rev.app.repository;

import com.rev.app.entity.Podcast;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class IPodcastRepositoryTest {

    @Autowired
    private IPodcastRepository podcastRepository;

    @Test
    void testFindByArtistId() {
        Podcast p1 = Podcast.builder().artistId(1).title("P1").build();
        Podcast p2 = Podcast.builder().artistId(1).title("P2").build();
        Podcast p3 = Podcast.builder().artistId(2).title("P3").build();
        podcastRepository.save(p1);
        podcastRepository.save(p2);
        podcastRepository.save(p3);

        List<Podcast> podcasts = podcastRepository.findByArtistId(1);
        assertEquals(2, podcasts.size());
    }
}
