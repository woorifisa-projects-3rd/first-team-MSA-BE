package com.example.Attendance.dto;

import com.example.Attendance.model.PayStatement;
import com.example.Attendance.model.StoreEmployee;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BatchOutputData {
    private Integer seId;
    private Integer status;
    private LocalDateTime issuanceDate;
    private String message;
    private Long amount;

    public BatchOutputData(Integer seId, Integer status, LocalDateTime issuanceDate, String message, Long amount) {
        this.seId = seId;
        this.status = status;
        this.issuanceDate = issuanceDate;
        this.message = message;
        this.amount = amount;
    }

    public static BatchOutputData of(Integer seId, Long amount, TransferResponse transferResponse) {

        return new BatchOutputData(seId, transferResponse.getStatus(), LocalDateTime.now(), transferResponse.getMessage(), amount);
    }

    public PayStatement toEntity() {
        return PayStatement.createPayStatementWithProxy("12342412", this.getIssuanceDate().toLocalDate(),this.getSeId(), this.getAmount().intValue());
    }
}