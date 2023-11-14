package com.example.americanbank.Repo;

import com.example.americanbank.Entity.CDAccount;
import com.example.americanbank.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CDAccountRepo extends JpaRepository<CDAccount, Long> {
    Optional<CDAccount> findByUser(User user);
}
