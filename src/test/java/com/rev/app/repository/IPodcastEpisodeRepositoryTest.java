package com.rev.app.repository;

import com.rev.app.entity.PodcastEpisode;
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
class IPodcastEpisodeRepositoryTest {

    @Autowired
    private IPodcastEpisodeRepository podcastEpisodeRepository;

    @Test
    void testFindByPodcastId() {
        PodcastEpisode e1 = PodcastEpisode.builder().podcastId(1).title("E1").durationSeconds(300).build();
        PodcastEpisode e2 = PodcastEpisode.builder().podcastId(1).title("E2").durationSeconds(400).build();
        PodcastEpisode e3 = PodcastEpisode.builder().podcastId(2).title("E3").durationSeconds(500).build();
        podcastEpisodeRepository.save(e1);
        podcastEpisodeRepository.save(e2);
        podcastEpisodeRepository.save(e3);

        List<PodcastEpisode> episodes = podcastEpisodeRepository.findByPodcastId(1);
        assertEquals(2, episodes.size());
    }
}
