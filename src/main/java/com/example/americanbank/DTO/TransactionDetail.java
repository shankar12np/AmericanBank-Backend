package com.example.americanbank.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {
    private double amount;
    private String type; //"DEPOSIT" or "WITHDRAW"
    private String dateAndTime;
}
