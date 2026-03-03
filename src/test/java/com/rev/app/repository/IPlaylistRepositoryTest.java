package com.rev.app.repository;

import com.rev.app.entity.Playlist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class IPlaylistRepositoryTest {

    @Autowired
    private IPlaylistRepository playlistRepository;

    @Test
    void testFindByUserId() {
        Playlist p1 = Playlist.builder().userId(1).name("P1").privacyStatus("public").build();
        Playlist p2 = Playlist.builder().userId(1).name("P2").privacyStatus("private").build();
        Playlist p3 = Playlist.builder().userId(2).name("P3").privacyStatus("public").build();
        playlistRepository.save(p1);
        playlistRepository.save(p2);
        playlistRepository.save(p3);

        List<Playlist> user1Playlists = playlistRepository.findByUserId(1);
        assertEquals(2, user1Playlists.size());
    }

    @Test
    void testFindByPrivacyStatus() {
        Playlist p1 = Playlist.builder().userId(1).name("P1").privacyStatus("public").build();
        Playlist p2 = Playlist.builder().userId(1).name("P2").privacyStatus("public").build();
        Playlist p3 = Playlist.builder().userId(2).name("P3").privacyStatus("private").build();
        playlistRepository.save(p1);
        playlistRepository.save(p2);
        playlistRepository.save(p3);

        List<Playlist> publicPlaylists = playlistRepository.findByPrivacyStatus("public");
        assertEquals(2, publicPlaylists.size());
    }
}
