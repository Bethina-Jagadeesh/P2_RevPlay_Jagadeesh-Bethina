package com.rev.app.repository;

import com.rev.app.entity.Podcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPodcastRepository extends JpaRepository<Podcast, Integer> {
    java.util.List<Podcast> findByArtistId(int artistId);
}
