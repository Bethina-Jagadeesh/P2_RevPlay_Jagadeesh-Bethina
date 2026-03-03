package com.rev.app.repository;

import com.rev.app.entity.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class IGenreRepositoryTest {

    @Autowired
    private IGenreRepository genreRepository;

    @Test
    void testSaveAndFindById() {
        Genre genre = Genre.builder().genreName("Rock").build();
        Genre saved = genreRepository.save(genre);

        Optional<Genre> found = genreRepository.findById(saved.getGenreId());
        assertTrue(found.isPresent());
        assertEquals("Rock", found.get().getGenreName());
    }
}
