package com.rev.app.repository;

import com.rev.app.entity.Album;
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
class IAlbumRepositoryTest {

    @Autowired
    private IAlbumRepository albumRepository;

    @Test
    void testFindByArtistId() {
        // Arrange
        Album album1 = Album.builder()
                .artistId(1)
                .title("Album 1")
                .build();
        Album album2 = Album.builder()
                .artistId(1)
                .title("Album 2")
                .build();
        Album album3 = Album.builder()
                .artistId(2)
                .title("Album 3")
                .build();
        albumRepository.save(album1);
        albumRepository.save(album2);
        albumRepository.save(album3);

        // Act
        List<Album> artist1Albums = albumRepository.findByArtistId(1);

        // Assert
        assertEquals(2, artist1Albums.size());
        assertTrue(artist1Albums.stream().anyMatch(a -> a.getTitle().equals("Album 1")));
        assertTrue(artist1Albums.stream().anyMatch(a -> a.getTitle().equals("Album 2")));
    }
}
