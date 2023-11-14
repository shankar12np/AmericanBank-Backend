package com.example.americanbank.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String transactionType; // e.g., "DEPOSIT" or "WITHDRAWAL"

    @CreationTimestamp
    private LocalDateTime transactionDateTime;

    // Foreign key to CheckingAccount
    @ManyToOne
    @JoinColumn(name = "new_checking_account_id", referencedColumnName = "id", nullable = true)
    private CheckingAccount checkingAccount;

    @ManyToOne
    @JoinColumn(name = "new_saving_account_id", referencedColumnName = "id", nullable = true)
    private SavingAccount savingAccount;
}