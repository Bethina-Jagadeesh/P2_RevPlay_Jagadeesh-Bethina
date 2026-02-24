package com.rev.app.repository;

import com.rev.app.entity.ArtistAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IArtistAccountRepository extends JpaRepository<ArtistAccount, Integer> {
    Optional<ArtistAccount> findByEmail(String email);

    @Query(value = "SELECT security_question FROM artist_account WHERE email = :email", nativeQuery = true)
    String findSecurityQuestionByEmail(@Param("email") String email);

    @Query(value = "SELECT security_answer_hash FROM artist_account WHERE email = :email", nativeQuery = true)
    String findSecurityAnswerHashByEmail(@Param("email") String email);

    @Modifying
    @Query(value = "UPDATE artist_account SET security_question = :q, security_answer_hash = :a WHERE artist_id = :id", nativeQuery = true)
    void saveSecurityInfo(@Param("id") int artistId, @Param("q") String question, @Param("a") String answerHash);
}
