package com.example.americanbank.Services;

import com.example.americanbank.Entity.*;
import com.example.americanbank.Repo.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AccountStatementService {

    @Autowired
    private AccountStatementRepo accountStatementRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CheckingAccountRepo checkingAccountRepo;
    @Autowired
    private SavingAccountRepo savingAccountRepo;
    @Autowired
    private CDAccountRepo cdAccountRepo;

    private final static Logger logger= LoggerFactory.getLogger(AccountStatementService.class);

    // Method to generate PDF statement for a single user with multiple accounts and transactions
    public byte[] generatePdfStatement(String username) throws Exception {
        // Fetch the user based on the provided username
        User user = userRepo.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Retrieve checking and saving accounts for the user
        Optional<CheckingAccount> checkingAccountOptional = checkingAccountRepo.findByUser(user);
        CheckingAccount checkingAccount = checkingAccountOptional.orElseThrow(() -> new RuntimeException("Checking account not found for the user"));

        Optional<SavingAccount> savingAccountOptional = savingAccountRepo.findByUser(user);
        SavingAccount savingAccount = savingAccountOptional.orElseThrow(() -> new RuntimeException("Saving account not found for the user"));

        Optional<CDAccount> cdAccountOptional = cdAccountRepo.findByUser(user);
        CDAccount cdAccount = cdAccountOptional.orElseThrow(() -> new RuntimeException("CD account not found for the user"));

        // Create a new Document instance for generating the PDF statement
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Set the output stream for the Document instance
        PdfWriter.getInstance(document, outputStream);

        // Open the Document for writing
        document.open();

        // Add the user's username as the document title
        document.add(new Paragraph("Account Statement for " + user.getUserName() + "\n"));

        // Add the current date and time
       // document.add(new Paragraph("Date: " + formatDateTime(LocalDateTime.now()) + "\n"));

        // Add user account details
        addAccountDetails(document, user, checkingAccount, savingAccount, cdAccount);

        // Add all checking account transactions
        List<Transaction> checkingTransactions = checkingAccount.getTransactions();
        addTransactions(document, "Checking Account Transactions", checkingTransactions);

        // Add all saving account transactions
        List<Transaction> savingTransactions = savingAccount.getTransactions();
        addTransactions(document, "Savings Account Transactions", savingTransactions);

        // Close the Document to finalize the PDF generation
        document.close();

        // Return the generated PDF as a byte array
        return outputStream.toByteArray();
    }

    private void addAccountDetails(Document document, User user, CheckingAccount checkingAccount, SavingAccount savingAccount, CDAccount cdAccount) throws DocumentException {
        document.add(new Paragraph("Date: " + formatDateTime(LocalDateTime.now()) + "\n"));
        document.add(new Paragraph("Account Details:\n"));
        document.add(new Paragraph("User Name: " + user.getUserName()));
        document.add(new Paragraph("Checking Account Balance: $" + checkingAccount.getBalance()));
        document.add(new Paragraph("Saving Account Balance: $" + savingAccount.getBalance()));
        document.add(new Paragraph("Checking Account Date and Time: " + formatDateTime(checkingAccount.getDateAndTime())));
        document.add(new Paragraph("Saving Account Date and Time: " + formatDateTime(savingAccount.getDateAndTime())));
        document.add(new Paragraph("Retrieval Date and Time: " + formatDateTime(LocalDateTime.now())));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Account Holder's Name: "+ user.getUserName()+ " - CD Account\n"));
        if (cdAccount != null) {
            document.add(new Paragraph("CD Account Balance: $" + cdAccount.getDepositAmount()));
            document.add(new Paragraph("CD Account Interest Rate: " + cdAccount.getInterestRate() + "%"));
            document.add(new Paragraph("CD Account Start Date: " + cdAccount.getStartDate()));
            document.add(new Paragraph("CD Account Maturity Date: " + cdAccount.getMaturityDate()));
        }
        document.add(new Paragraph("\n"));
    }

    private void addTransactions(Document document, String sectionHeader, List<Transaction> transactions) throws DocumentException {
        document.add(new Paragraph(sectionHeader + ":\n"));
        for (Transaction transaction : transactions) {
            document.add(new Paragraph("Amount: $" + transaction.getAmount()));
            document.add(new Paragraph("Type: " + transaction.getTransactionType()));
            document.add(new Paragraph("Date and Time: " +formatDateTime(transaction.getTransactionDateTime())));
            document.add(new Paragraph("\n"));
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy, hh:mma");
        return dateTime.format(formatter);
    }


    public List<AccountStatement> findBySavingAccountId(Long accountId) {
        return savingAccountRepo.findById(accountId)
                .map(accountStatementRepo::findBySavingAccount)
                .orElse(Collections.emptyList());
    }

    public List<AccountStatement> findByCheckingAccountId(Long accountId) {
        return checkingAccountRepo.findById(accountId)
                .map(accountStatementRepo::findByCheckingAccount)
                .orElse(Collections.emptyList());
    }
    public byte[] generatePdfStatementsForAll(List<User> users) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Iterate over all users
        for (User user : users) {
            // Your logic to add user's account statements to the document
            Optional<CheckingAccount> checkingAccountOptional = checkingAccountRepo.findByUser(user);
            Optional<SavingAccount> savingAccountOptional = savingAccountRepo.findByUser(user);

            // Add checking account transactions if present
            checkingAccountOptional.ifPresent(account -> {
                // Add statements from the checking account to the document
                // ...
            });

            // Add saving account transactions if present
            savingAccountOptional.ifPresent(account -> {
                // Add statements from the saving account to the document
                // ...
            });

            // Add some delimiter or page break between different users' statements
            document.newPage();
        }

        document.close();
        return outputStream.toByteArray();
    }
}

