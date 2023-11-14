package com.example.americanbank.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_statement")
public class AccountStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "new_checking_account_id", referencedColumnName = "id")
    private CheckingAccount checkingAccount;

    @ManyToOne
    @JoinColumn(name = "new_saving_account_id", referencedColumnName = "id")
    private SavingAccount savingAccount;

    @OneToOne
    @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    private Transaction transaction;

    private LocalDateTime transactionDate;
    private String transactionType;
    private Double amount;
    private Double balanceAfterTransaction;
}
