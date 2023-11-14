package com.example.americanbank;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AccountOperationException extends RuntimeException{
    public AccountOperationException(String message) {
        super(message);
    }
}
