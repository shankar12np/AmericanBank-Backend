package com.example.americanbank.Services;

import com.example.americanbank.AccountOperationException;
import com.example.americanbank.DTO.AccountDetails;
import com.example.americanbank.DTO.AccountOpeningRequest;
import com.example.americanbank.DTO.CDAccountOpeningRequest;
import com.example.americanbank.DTO.TransactionDetail;
import com.example.americanbank.Entity.*;
import com.example.americanbank.GlobalExceptionHandler.AccountAlreadyExistException;
import com.example.americanbank.Repo.*;
import com.example.americanbank.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    @Autowired
    private CheckingAccountRepo checkingAccountRepo;
    @Autowired
    private SavingAccountRepo savingAccountRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private CDAccountRepo cdAccountRepo;

    @Transactional
    public void openCheckingAccount(AccountOpeningRequest request) throws Exception {
        String userName = request.getUserName();
        double initialDeposit = request.getInitialDeposit();
        String ssn = request.getSsn();
        // Add more fields from the request as needed

        Optional<User> userOptional = userRepo.findByUserName(userName);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User must be registered to open an account");
        }

        User user = userOptional.get();

        // Check if user already has a checking account
        if (!user.getCheckingAccounts().isEmpty()) {
            throw new AccountAlreadyExistException("User already has checking account");
        }

        // Create new checking account
        CheckingAccount checkingAccount = new CheckingAccount();
        checkingAccount.setBalance(initialDeposit); // use initialDeposit here
        checkingAccount.setDateAndTime(LocalDateTime.now());

        // Associate account with user
        checkingAccount.setUser(user);

        // Optionally, you can also add the new account to the user's list of checking accounts
        user.getCheckingAccounts().add(checkingAccount);

        // Save the new account
        checkingAccountRepo.save(checkingAccount);
    }

    @Transactional
    public void openSavingAccount(AccountOpeningRequest request) throws Exception {
        String userName = request.getUserName();
        double initialDeposit = request.getInitialDeposit();
        String ssn = request.getSsn();
        // Add more fields from the request as needed

        Optional<User> userOptional = userRepo.findByUserName(userName);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User must be registered to open an account");
        }

        User user = userOptional.get();

        // Check if user already have saving ccount

        if (!user.getSavingAccounts().isEmpty()) {
            throw new AccountAlreadyExistException("User already has saving account");
        }

        // Create new saving account
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setBalance(initialDeposit); // use initialDeposit here
        savingAccount.setDateAndTime(LocalDateTime.now());

        // Associate account with user
        savingAccount.setUser(user);

        // Optionally, you can add the new account to the user's list of saving accounts
        user.getSavingAccounts().add(savingAccount);

        savingAccountRepo.save(savingAccount);
    }

    public void depositToChecking(String userName, Double amount) {
        Optional<User> userOptional = userRepo.findByUserName(userName);
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<CheckingAccount> checkingAccountOptional = checkingAccountRepo.findByUser(user);
        CheckingAccount checkingAccount = checkingAccountOptional.orElseThrow(() -> new AccountOperationException("Checking account does not exist for the user"));

        // proceed with deposit
        double newBalance = checkingAccount.getBalance() + amount;
        checkingAccount.setBalance(newBalance);

        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("DEPOSIT");
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setCheckingAccount(checkingAccount);

        checkingAccount.getTransactions().add(transaction);

        checkingAccountRepo.save(checkingAccount);
        transactionRepo.save(transaction);
    }

    @Transactional
    public void depositToSaving(String userName, Double amount) throws Exception {
        Optional<User> userOptional = userRepo.findByUserName(userName);
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<SavingAccount> savingAccountOptional = savingAccountRepo.findByUser(user);
        SavingAccount savingAccount = savingAccountOptional.orElseThrow(() -> new AccountOperationException("Saving account does not exist for the user"));

        // proceed with deposit
        double newBalance = savingAccount.getBalance() + amount;
        savingAccount.setBalance(newBalance);

        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("DEPOSIT");
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setSavingAccount(savingAccount);

        savingAccount.getTransactions().add(transaction);

        savingAccountRepo.save(savingAccount);
        transactionRepo.save(transaction);
    }

    public AccountDetails getAccountDetails(String userName) {
        try {
            Optional<User> userOptional = userRepo.findByUserName(userName);
            User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found"));

            Optional<CheckingAccount> checkingAccountOpt = checkingAccountRepo.findByUser(user);
            Optional<SavingAccount> savingAccountOpt = savingAccountRepo.findByUser(user);
            Optional<CDAccount> cdAccountOptional = cdAccountRepo.findByUser(user);

            AccountDetails accountDetails = new AccountDetails();
            accountDetails.setUserName(userName);

            // Populate Checking Account details including transactions
            if (checkingAccountOpt.isPresent()) {
                CheckingAccount checkingAccount = checkingAccountOpt.get();
                accountDetails.setCheckingBalance(checkingAccount.getBalance());
                accountDetails.setCheckingAccountTransactions(
                        checkingAccount.getTransactions().stream()
                                .map(this::convertToTransactionDetail)
                                .collect(Collectors.toList())
                );
            } else {
                accountDetails.setCheckingBalance(0.00);
            }

            // Populate Saving Account details including transactions
            if (savingAccountOpt.isPresent()) {
                SavingAccount savingAccount = savingAccountOpt.get();
                accountDetails.setSavingBalance(savingAccount.getBalance());
                accountDetails.setSavingAccountTransactions(
                        savingAccount.getTransactions().stream()
                                .map(this::convertToTransactionDetail)
                                .collect(Collectors.toList())
                );
            } else {
                accountDetails.setSavingBalance(0.00);
            }
// Populated CD Account details;

            if (cdAccountOptional.isPresent()){
                CDAccount cdAccount = cdAccountOptional.get();
                accountDetails.setCdAccountBalance(cdAccount.getDepositAmount());
                accountDetails.setCdAccountInterest(cdAccount.getInterestRate());
                accountDetails.setCdAccountMaturityDate(cdAccount.getMaturityDate()); // Direct assignment
                accountDetails.setCdAccountStartDate(cdAccount.getStartDate()); // Direct assignment
                accountDetails.setCdAccountStatus(cdAccount.getStatus());
            } else {
                accountDetails.setCdAccountBalance(accountDetails.getCdAccountBalance());
            }

            accountDetails.setRetrievalDateAndTime(formatDateTime(LocalDateTime.now()));

            return accountDetails;
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", userName, e);
            throw e; // You can create a custom response entity or exception to handle this appropriately
        } catch (Exception e) {
            logger.error("Error getting account details for user: {}", userName, e);
            throw new RuntimeException("Internal server error"); // Or a more specific custom exception
        }
    }

    private TransactionDetail convertToTransactionDetail(Transaction transaction) {
        return new TransactionDetail(
                transaction.getAmount(),
                transaction.getTransactionType(),
                formatDateTime(transaction.getTransactionDateTime()) // Here it's used
        );
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy, hh:mma");
        return dateTime.format(formatter);
    }

    @Transactional
    public void withdrawFromChecking(String userName, double amount) throws Exception {
        Optional<User> userOptional = userRepo.findByUserName(userName);
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<CheckingAccount> checkingAccountOptional = user.getCheckingAccounts().stream()
                .findFirst();
        CheckingAccount checkingAccount = checkingAccountOptional.orElseThrow(() -> new AccountOperationException("No checking account found for the user"));

        double currentBalance = checkingAccount.getBalance();
        if (currentBalance < amount) {
            throw new AccountOperationException("Insufficient balance");
        }

        checkingAccount.setBalance(currentBalance - amount);
        checkingAccountRepo.save(checkingAccount);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("WITHDRAWAL");
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setCheckingAccount(checkingAccount);
        transactionRepo.save(transaction);
    }

    @Transactional
    public void withdrawFromSaving(String userName, double amount) throws Exception {
        Optional<User> userOptional = userRepo.findByUserName(userName);
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<SavingAccount> savingAccountOptional = user.getSavingAccounts().stream()
                .findFirst();
        SavingAccount savingAccount = savingAccountOptional.orElseThrow(() -> new AccountOperationException("No saving account found for the user"));

        double currentBalance = savingAccount.getBalance();
        if (currentBalance < amount) {
            throw new AccountOperationException("Insufficient balance");
        }

        // Deduct amount from balance
        savingAccount.setBalance(currentBalance - amount);
        savingAccountRepo.save(savingAccount);

        // Record the withdrawal transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("WITHDRAWAL");
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setSavingAccount(savingAccount);
        transactionRepo.save(transaction);
    }

    public void openCDAccount(CDAccountOpeningRequest request) throws Exception {
        String userName = request.getUserName();
        Long depositAmount = request.getDepositAmount();
        double interestRate = request.getInterestRate();
        String maturityDate = request.getMaturityDate();
       // String startDate = request.getStartDate();

        Optional<User> userOptional = userRepo.findByUserName(userName);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User be registered to open a CD account");
        }
        User user = userOptional.get();
        // Check if user already has a checking account
        if (!user.getCdAccounts().isEmpty()) {
            throw new AccountAlreadyExistException("User already have CD Account");
        }
        //Create new CD Account
        CDAccount cdAccount = new CDAccount();
        cdAccount.setDepositAmount(depositAmount);
        cdAccount.setInterestRate(interestRate);
        cdAccount.setMaturityDate(request.getMaturityDate());
        cdAccount.setUser(user);
        cdAccount.setStartDate(formatDateTime(LocalDateTime.now()));
        cdAccount.setStatus("Active");
        cdAccountRepo.save(cdAccount);

    }


}
