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
}
