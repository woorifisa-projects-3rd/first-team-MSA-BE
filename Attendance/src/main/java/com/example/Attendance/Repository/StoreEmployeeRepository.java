package com.example.Attendance.Repository;

import com.example.Attendance.model.StoreEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreEmployeeRepository extends JpaRepository<StoreEmployee, Integer> {

    //이것도 필요한거 dto로 보내게끔
    @Query("select se from StoreEmployee se join fetch se.store s  where se.email= :email and s.id= :storeId")
    Optional<StoreEmployee> findByEmailAndStoreId
            (@Param("email") String email, @Param("storeId") Integer storeId);
}
