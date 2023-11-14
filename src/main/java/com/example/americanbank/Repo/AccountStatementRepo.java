package com.example.americanbank.Repo;

import com.example.americanbank.Entity.AccountStatement;
import com.example.americanbank.Entity.CheckingAccount;
import com.example.americanbank.Entity.SavingAccount;
import com.example.americanbank.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountStatementRepo extends JpaRepository<AccountStatement, Long> {
    List<AccountStatement> findByCheckingAccount(CheckingAccount checkingAccount);

    List<AccountStatement> findBySavingAccount(SavingAccount savingAccount);


    List<AccountStatement> findByUser(User user);
}
