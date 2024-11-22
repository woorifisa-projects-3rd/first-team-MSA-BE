package com.example.User.repository;

import com.example.User.model.StoreEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StoreEmployeeRepository extends JpaRepository<StoreEmployee, Integer> {
    @Modifying
    @Query("UPDATE StoreEmployee se " +"SET se.name = :name, " + "se.sex = :sex, " +
            "se.address = :address, " + "se.birthDate = :birthDate, " + "se.phoneNumber = :phoneNumber, " +
            "se.email = :email, " + "se.salary = :salary, " + "se.employmentType = :employmentType, " +
            "se.bankCode = :bankCode, " + "se.accountNumber = :accountNumber, " + "se.paymentDate = :paymentDate " +
            "WHERE se.id = :id")
    void updateStoreEmployee(@Param("id") Integer id, @Param("name") String name, @Param("sex") Boolean sex,
            @Param("address") String address, @Param("birthDate") LocalDate birthDate, @Param("phoneNumber") String phoneNumber,
            @Param("email") String email, @Param("salary") Integer salary, @Param("employmentType") Byte employmentType, @Param("bankCode") String bankCode,
            @Param("accountNumber") String accountNumber, @Param("paymentDate") Integer paymentDate);

    @Query("SELECT se FROM StoreEmployee se JOIN FETCH se.store WHERE se.store.id = :storeId")
    List<StoreEmployee> findByStoreIdWithFetch(@Param("storeId") Integer storeId);

    @Query("UPDATE StoreEmployee se " +
            "SET se.bankCode = '000', " +
            "se.accountNumber = '탈퇴하여 계좌 정보를 가져올 수 없습니다.', " +
            "se.email = '*****@*****.***', " +
            "se.phoneNumber = '***-****-****', " +
            "se.address = '탈퇴한 회원입니다.' " +
            "WHERE se.id = :seId")
    @Modifying
    void updateEmployeeReplaceDelete(@Param("seId") Integer seId);
}
