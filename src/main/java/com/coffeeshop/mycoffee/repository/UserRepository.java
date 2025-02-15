package com.coffeeshop.mycoffee.repository;

import com.coffeeshop.mycoffee.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);
}
