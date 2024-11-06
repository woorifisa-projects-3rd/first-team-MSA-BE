package com.example.User.respository;

import com.example.User.model.President;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresidentRepository extends JpaRepository<President, Integer> {
}
