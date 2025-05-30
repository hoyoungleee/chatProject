package com.example.demo.domain.repository;

import com.example.demo.domain.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByname(String name) ;
    boolean existsByName(String name);
}
