package com.example.User.repository;

import com.example.User.model.President;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PresidentRepository extends JpaRepository<President, Integer> {
    Optional<President> findByEmail(String email);

    boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

    void deleteByEmail(String email);

    Optional<President> findByNameAndPhoneNumber(String name, String phoneNumber);

    //사장 정보 수정 쿼리 요청
    @Modifying
    @Transactional
    @Query("UPDATE President p SET p.phoneNumber = :phoneNumber, p.birthDate = :birthDate WHERE p.id = :id")
    int updatePhoneNumberAndBirthDate(@Param("id") Integer id,
                                      @Param("phoneNumber") String phoneNumber,
                                      @Param("birthDate") LocalDate birthDate);

    //jwt토큰에서 id로사장 name추출??
     @Query("SELECT p.name FROM President p WHERE p.id = :id")
     Optional<String> findNameById(@Param("id") Integer id);

}

