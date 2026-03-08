package com.rev.app.repository;

import com.rev.app.entity.UserAccount;
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
class UserAccountRepositoryTest {

    @Autowired
    private IUserAccountRepository userRepository;

    @Test
    void testFindByEmail_Success() {
        // Arrange
        UserAccount user = UserAccount.builder()
                .email("repo@test.com")
                .passwordHash("hashed")
                .fullName("Test User")
                .build();
        userRepository.save(user);

        // Act
        Optional<UserAccount> found = userRepository.findByEmail("repo@test.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getFullName());
    }

    @Test
    void testFindByEmail_NotFound() {
        // Act
        Optional<UserAccount> found = userRepository.findByEmail("non_existent@email.com");

        // Assert
        assertFalse(found.isPresent());
    }
}
