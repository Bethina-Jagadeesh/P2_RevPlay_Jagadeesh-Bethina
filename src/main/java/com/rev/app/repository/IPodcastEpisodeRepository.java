package com.rev.app.repository;

import com.rev.app.entity.PodcastEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPodcastEpisodeRepository extends JpaRepository<PodcastEpisode, Integer> {
    java.util.List<PodcastEpisode> findByPodcastId(int podcastId);
}
