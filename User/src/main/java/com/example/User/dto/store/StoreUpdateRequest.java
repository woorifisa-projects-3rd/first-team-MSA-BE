package com.example.User.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreUpdateRequest {

    @NotBlank
    private String storeName;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String location;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}
