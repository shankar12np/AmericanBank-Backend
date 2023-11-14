package com.example.americanbank.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CDAccountOpeningRequest {
    private String userName;
    private Long depositAmount;
    private double interestRate;
    private String maturityDate;
    private String startDate;
}
