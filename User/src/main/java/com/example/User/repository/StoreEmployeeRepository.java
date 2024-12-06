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
    int updateStoreEmployee(@Param("id") Integer id, @Param("name") String name, @Param("sex") Boolean sex,
            @Param("address") String address, @Param("birthDate") LocalDate birthDate, @Param("phoneNumber") String phoneNumber,
            @Param("email") String email, @Param("salary") Integer salary, @Param("employmentType") Byte employmentType, @Param("bankCode") String bankCode,
            @Param("accountNumber") String accountNumber, @Param("paymentDate") Integer paymentDate);

    @Query("SELECT se FROM StoreEmployee se JOIN FETCH se.store WHERE se.store.id = :storeId")
    List<StoreEmployee> findByStoreIdWithFetch(@Param("storeId") Integer storeId);

    @Query("UPDATE StoreEmployee se " +
            "SET se.employmentType = 10 " +
            "WHERE se.id = :seId")
    @Modifying
    void updateEmployeeReplaceDelete(@Param("seId") Integer seId);

    @Modifying
    @Query("UPDATE StoreEmployee se SET se.employmentType = :type WHERE se.id IN :ids")
    int updateEmploymentTypeByIds(@Param("ids") List<Integer> ids, @Param("type") Byte type);
}
