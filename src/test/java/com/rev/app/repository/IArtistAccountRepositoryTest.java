package com.rev.app.repository;

import com.rev.app.entity.ArtistAccount;
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
class IArtistAccountRepositoryTest {

    @Autowired
    private IArtistAccountRepository artistRepository;

    @Test
    void testFindByEmail_Success() {
        // Arrange
        ArtistAccount artist = ArtistAccount.builder()
                .stageName("Artist 1")
                .email("artist@test.com")
                .passwordHash("hashed")
                .build();
        artistRepository.save(artist);

        // Act
        Optional<ArtistAccount> found = artistRepository.findByEmail("artist@test.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Artist 1", found.get().getStageName());
    }

    @Test
    void testFindByEmail_NotFound() {
        // Act
        Optional<ArtistAccount> found = artistRepository.findByEmail("non_existent@test.com");

        // Assert
        assertFalse(found.isPresent());
    }
}
