package com.rev.app.repository;

import com.rev.app.entity.ListeningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IListeningHistoryRepository extends JpaRepository<ListeningHistory, Integer> {
    List<ListeningHistory> findByUserIdOrderByPlayedAtDesc(int userId);

    @Transactional
    void deleteByUserId(int userId);

    @Transactional
    List<ListeningHistory> findBySongIdIn(List<Integer> songIds);

    @Transactional
    void deleteBySongId(int songId);

}
