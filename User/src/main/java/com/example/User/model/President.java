package com.example.User.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "president")
@Getter
@NoArgsConstructor
public class President {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "president_id")
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 150)
    private String address;

    @Column(name = "business_number", nullable = false, length = 50, unique = true)
    private String businessNumber;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "account_number", nullable = false, length = 50, unique = true)
    private String accountNumber;

    @Column(name = "phone_number", nullable = false, length = 50, unique = true)
    private String phoneNumber;

    @Column(name = "temrs_accept", nullable = false)
    private Boolean termsAccept;

    @OneToMany(mappedBy = "president")
    private List<Employee> employees = new ArrayList<>();

    private President(String name, String address, String businessNumber, String email,
                      LocalDate birthDate, String accountNumber, String phoneNumber,
                      Boolean termsAccept, List<Employee> employees) {
        this.name = name;
        this.address = address;
        this.businessNumber = businessNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.accountNumber = accountNumber;
        this.phoneNumber = phoneNumber;
        this.termsAccept = termsAccept;
        this.employees = employees;
    }

    public static President createPresident(String name, String address, String businessNumber, String email,
                                            LocalDate birthDate, String accountNumber, String phoneNumber,
                                            Boolean termsAccept, List<Employee> employees) {
        return new President(name, address, businessNumber, email, birthDate, accountNumber, phoneNumber, termsAccept, employees);
    }
}