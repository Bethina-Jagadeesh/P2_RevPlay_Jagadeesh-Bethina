package com.rev.app.repository;

import com.rev.app.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByEmail(String email);

    java.util.List<UserAccount> findByUserIdIn(java.util.List<Integer> userIds);

}
