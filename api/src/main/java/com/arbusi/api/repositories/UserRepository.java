package com.arbusi.api.repositories;

import com.arbusi.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByAuthSourceUserId(String authSourceUserId);

    boolean existsByEmail(String email);
}
