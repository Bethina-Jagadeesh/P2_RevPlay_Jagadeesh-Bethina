package com.rev.app.repository;

import com.rev.app.entity.FavoriteSong;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class IFavoriteSongRepositoryTest {

    @Autowired
    private IFavoriteSongRepository favoriteSongRepository;

    @Test
    void testFindByUserId() {
        FavoriteSong fs1 = FavoriteSong.builder().userId(1).songId(101).favoritedAt(LocalDateTime.now()).build();
        FavoriteSong fs2 = FavoriteSong.builder().userId(1).songId(102).favoritedAt(LocalDateTime.now()).build();
        favoriteSongRepository.save(fs1);
        favoriteSongRepository.save(fs2);

        List<FavoriteSong> favorites = favoriteSongRepository.findByUserId(1);
        assertEquals(2, favorites.size());
    }

    @Test
    void testExistsByUserIdAndSongId() {
        FavoriteSong fs = FavoriteSong.builder().userId(1).songId(101).build();
        favoriteSongRepository.save(fs);

        assertTrue(favoriteSongRepository.existsByUserIdAndSongId(1, 101));
        assertFalse(favoriteSongRepository.existsByUserIdAndSongId(1, 102));
    }

    @Test
    void testDeleteByUserIdAndSongId() {
        FavoriteSong fs = FavoriteSong.builder().userId(1).songId(101).build();
        favoriteSongRepository.save(fs);
        assertTrue(favoriteSongRepository.existsByUserIdAndSongId(1, 101));

        favoriteSongRepository.deleteByUserIdAndSongId(1, 101);
        assertFalse(favoriteSongRepository.existsByUserIdAndSongId(1, 101));
    }

    @Test
    void testCountBySongIdIn() {
        FavoriteSong fs1 = FavoriteSong.builder().userId(1).songId(101).build();
        FavoriteSong fs2 = FavoriteSong.builder().userId(2).songId(101).build();
        FavoriteSong fs3 = FavoriteSong.builder().userId(1).songId(102).build();
        favoriteSongRepository.saveAll(Arrays.asList(fs1, fs2, fs3));

        long count = favoriteSongRepository.countBySongIdIn(Arrays.asList(101, 102));
        assertEquals(3, count);
    }
}
