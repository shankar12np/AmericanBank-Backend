package com.example.americanbank.DTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountOpeningRequest {
    private String userName;
    private double initialDeposit;
    @Pattern(regexp = "^(?!000|666|9\\d\\d)\\d{3}-(?!00)\\d{2}-(?!0000)\\d{4}$", message = "Invalid SSN format")
    private String ssn;
}
