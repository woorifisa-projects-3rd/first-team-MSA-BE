package com.example.User.service;



import com.example.User.model.President;
import com.example.User.repository.PresidentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class APIUserDetailsService  {

    //주입
    private final PresidentRepository presidentRepository;


    public President loadUserByUsername(String username)  {

        President president = presidentRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find email"));

        log.info("APIUserDetailsService apiUser-------------------------------------");

//        List<SimpleGrantedAuthority> roles = new ArrayList<>();
//        roles.add(new SimpleGrantedAuthority("ROLE_USER"));

//        President customUserDetail = CustomUserDetail.
//                of(apiUser.getId(), apiUser.getEmail(), apiUser.getPassword(), roles);
//        log.info(String.valueOf(customUserDetail));

        return president;
    }

    public President findByEmail(String email) {
        return presidentRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find email"));
    }
}
