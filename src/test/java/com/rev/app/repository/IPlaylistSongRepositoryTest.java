package com.rev.app.repository;

import com.rev.app.entity.PlaylistSong;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class IPlaylistSongRepositoryTest {

    @Autowired
    private IPlaylistSongRepository playlistSongRepository;

    @Test
    void testFindByPlaylistId() {
        PlaylistSong ps1 = PlaylistSong.builder().playlistId(1).songId(101).addedAt(LocalDateTime.now()).build();
        PlaylistSong ps2 = PlaylistSong.builder().playlistId(1).songId(102).addedAt(LocalDateTime.now()).build();
        playlistSongRepository.save(ps1);
        playlistSongRepository.save(ps2);

        List<PlaylistSong> songs = playlistSongRepository.findByPlaylistId(1);
        assertEquals(2, songs.size());
    }

    @Test
    void testDeleteByPlaylistIdAndSongId() {
        PlaylistSong ps = PlaylistSong.builder().playlistId(1).songId(101).build();
        playlistSongRepository.save(ps);

        playlistSongRepository.deleteByPlaylistIdAndSongId(1, 101);
        List<PlaylistSong> songs = playlistSongRepository.findByPlaylistId(1);
        assertTrue(songs.isEmpty());
    }
}
