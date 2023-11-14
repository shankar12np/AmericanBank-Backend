package com.example.americanbank.Controller;

import com.example.americanbank.DTO.AccountDetails;
import com.example.americanbank.DTO.AccountOpeningRequest;
import com.example.americanbank.DTO.CDAccountOpeningRequest;
import com.example.americanbank.DTO.TransactionRequest;
import com.example.americanbank.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/open/checking")
    public ResponseEntity<String> openCheckingAccount(@RequestBody AccountOpeningRequest request) {
        try {
            accountService.openCheckingAccount(request);
            return ResponseEntity.ok("Checking account opened successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/open/saving")
    public ResponseEntity<String> openSavingAccount(@RequestBody AccountOpeningRequest request) {
        try {
            accountService.openSavingAccount(request);
            return ResponseEntity.ok("Saving account opened successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/open/CD")
    public ResponseEntity<String> openCDAccount(@RequestBody CDAccountOpeningRequest request){
        try {
            accountService.openCDAccount(request);
            return ResponseEntity.ok("CD account opened successfully");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/checking/deposit")
    public ResponseEntity<String> depositToChecking(@RequestBody TransactionRequest request) {
        try {
            accountService.depositToChecking(request.getUserName(), request.getAmount());
            return ResponseEntity.ok("Deposit successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/saving/deposit")
    public ResponseEntity<String> depositToSaving(@RequestBody TransactionRequest request) {
        try {
            accountService.depositToSaving(request.getUserName(), request.getAmount());
            return ResponseEntity.ok("Paisa Save Bhayo");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/details")
    public ResponseEntity<AccountDetails> getAccountDetails(@RequestParam String userName) {
        AccountDetails accountDetails = accountService.getAccountDetails(userName);
        return ResponseEntity.ok(accountDetails);
    }

    @PostMapping("/checking/withdraw")
    public ResponseEntity<String> withdrawFromChecking(@RequestBody TransactionRequest request) {
        try {
            accountService.withdrawFromChecking(request.getUserName(), request.getAmount());
            return ResponseEntity.ok("Withdrawal successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/saving/withdraw")
    public ResponseEntity<String> withdrawFromSaving(@RequestBody TransactionRequest request) {
        try {
            accountService.withdrawFromSaving(request.getUserName(), request.getAmount());
            return ResponseEntity.ok("Withdrawal successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
