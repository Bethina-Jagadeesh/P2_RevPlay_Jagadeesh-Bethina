package com.rev.app.repository;

import com.rev.app.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPlaylistRepository extends JpaRepository<Playlist, Integer> {
    List<Playlist> findByUserId(int userId);

    List<Playlist> findByPrivacyStatus(String privacyStatus);
}
