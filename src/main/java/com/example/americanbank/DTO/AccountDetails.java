package com.example.americanbank.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {
    private String userName;
    private Double checkingBalance;
    private Double savingBalance;
    @JsonIgnore
    private String checkingAccountDateAndTime;
    @JsonIgnore
    private String savingAccountDateAndTime;
    private String retrievalDateAndTime;
    private List<TransactionDetail> checkingAccountTransactions;
    private List<TransactionDetail> savingAccountTransactions;

    private Long cdAccountBalance;
    private double cdAccountInterest;
    private String cdAccountMaturityDate;
    private String cdAccountStartDate;
    private String cdAccountStatus;
    // add methods to set formatted dates
    public void setCheckingAccountDateAndTime(String dateTime) {
        this.checkingAccountDateAndTime = dateTime;
    }

    public void setSavingAccountDateAndTime(String dateTime) {
        this.savingAccountDateAndTime = dateTime;
    }

    public void setRetrievalDateAndTime(String dateTime) {
        this.retrievalDateAndTime = dateTime;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy, hh:mma");
        return dateTime.format(formatter);
    }
}
