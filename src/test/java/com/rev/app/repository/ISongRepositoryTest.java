package com.rev.app.repository;

import com.rev.app.entity.Song;
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
class ISongRepositoryTest {

    @Autowired
    private ISongRepository songRepository;

    @Test
    void testFindByArtistId() {
        Song s = Song.builder().artistId(1).genreId(1).title("Song 1").durationSeconds(200).build();
        songRepository.save(s);
        List<Song> songs = songRepository.findByArtistId(1);
        assertFalse(songs.isEmpty());
    }

    @Test
    void testFindByGenreId() {
        Song s = Song.builder().artistId(1).genreId(5).title("Song 5").durationSeconds(200).build();
        songRepository.save(s);
        List<Song> songs = songRepository.findByGenreId(5);
        assertFalse(songs.isEmpty());
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        Song s = Song.builder().artistId(1).genreId(1).title("Hello World").durationSeconds(200).build();
        songRepository.save(s);
        List<Song> songs = songRepository.findByTitleContainingIgnoreCase("HELLO");
        assertFalse(songs.isEmpty());
    }
}
