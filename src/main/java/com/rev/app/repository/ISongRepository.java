package com.rev.app.repository;

import com.rev.app.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISongRepository extends JpaRepository<Song, Integer> {
    List<Song> findByArtistId(int artistId);

    List<Song> findByGenreId(int genreId);

    List<Song> findByAlbumId(int albumId);

    List<Song> findByTitleContainingIgnoreCase(String keyword);

    List<Song> findAllByOrderByPlayCountDesc();

    List<Song> findByIsActive(String isActive);
}
