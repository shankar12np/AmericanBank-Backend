package com.example.americanbank.Controller;

import com.example.americanbank.Repo.UserRepo;
import com.example.americanbank.Services.AccountStatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statements")
@CrossOrigin(origins = "http://localhost:4200")
public class AccountStatementController {

    private static final Logger logger = LoggerFactory.getLogger(AccountStatementController.class);

    @Autowired
    private AccountStatementService accountStatementService;
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/download/{username}")
    public ResponseEntity<ByteArrayResource> downloadStatement(@PathVariable String username) {
        try {
            byte[] data = accountStatementService.generatePdfStatement(username);
            ByteArrayResource resource = new ByteArrayResource(data);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statement_" + username + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            logger.error("Failed to download statement for user {}: ", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}