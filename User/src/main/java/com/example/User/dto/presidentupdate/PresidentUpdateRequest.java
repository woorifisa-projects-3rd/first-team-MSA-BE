package com.example.User.dto.presidentupdate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresidentUpdateRequest {

    @NotBlank
    private String phoneNumber;

    @NotNull
    private LocalDate birthDate;

}
