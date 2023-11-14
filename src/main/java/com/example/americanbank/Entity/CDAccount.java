package com.example.americanbank.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cd_account")
public class CDAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cdAccountId;
    private Long depositAmount;
    private double interestRate;
    private String maturityDate;
    private String startDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_name")
    private User user;
}
