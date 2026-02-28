package com.rev.app.repository;

import com.rev.app.entity.PodcastEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface IPodcastEpisodeRepository extends JpaRepository<PodcastEpisode, Integer> {
    Collection<Object> findByPodcastId(int podcastId);
}
