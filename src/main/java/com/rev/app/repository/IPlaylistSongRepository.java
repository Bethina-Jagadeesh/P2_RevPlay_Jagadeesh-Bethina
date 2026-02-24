package com.rev.app.repository;

import com.rev.app.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IPlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSong.PlaylistSongId> {
    List<PlaylistSong> findByPlaylistId(int playlistId);

    @Transactional
    void deleteByPlaylistIdAndSongId(int playlistId, int songId);
}
