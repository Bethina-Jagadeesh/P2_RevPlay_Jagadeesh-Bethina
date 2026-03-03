package com.rev.app.repository;

import com.rev.app.entity.FavoriteSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFavoriteSongRepository extends JpaRepository<FavoriteSong, FavoriteSong.FavoriteSongId> {
    List<FavoriteSong> findByUserId(int userId);

    boolean existsByUserIdAndSongId(int userId, int songId);

    @Transactional
    void deleteByUserIdAndSongId(int userId, int songId);

    long countBySongIdIn(List<Integer> songIds);

    List<FavoriteSong> findBySongIdIn(List<Integer> songIds);

    @Transactional
    void deleteBySongId(int songId);
}
