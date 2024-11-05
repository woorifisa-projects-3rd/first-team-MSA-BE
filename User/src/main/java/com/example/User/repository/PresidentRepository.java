package com.example.User.repository;

import com.example.User.model.President;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PresidentRepository extends JpaRepository<President, Integer> {
    Optional<President> findByEmail(String email);
}
