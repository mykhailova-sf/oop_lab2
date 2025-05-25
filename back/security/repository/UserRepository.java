package com.laba.products.security.repository;

import com.laba.products.app.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(@NotBlank String username);


    boolean existsUserByUsername(@NotBlank String username);

    void deleteByUsername(@NotBlank String username);

}
