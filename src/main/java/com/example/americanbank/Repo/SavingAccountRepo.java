package com.example.americanbank.Repo;

import com.example.americanbank.Entity.SavingAccount;
import com.example.americanbank.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavingAccountRepo extends JpaRepository<SavingAccount, Long> {
    Optional<SavingAccount> findById(Long id);
    Optional<SavingAccount> findByUser(User user);
}
