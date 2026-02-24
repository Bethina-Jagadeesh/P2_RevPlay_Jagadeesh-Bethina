package com.rev.app.repository;

import com.rev.app.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAlbumRepository extends JpaRepository<Album, Integer> {
    List<Album> findByArtistId(int artistId);
}
