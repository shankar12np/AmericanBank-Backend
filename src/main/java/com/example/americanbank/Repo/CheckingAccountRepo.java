package com.example.americanbank.Repo;

import com.example.americanbank.Entity.CheckingAccount;
import com.example.americanbank.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckingAccountRepo extends JpaRepository<CheckingAccount, Long> {
    Optional<CheckingAccount> findById(Long id);
    Optional<CheckingAccount> findByUser(User user);
}
