package com.example.americanbank.GlobalExceptionHandler;

public class AccountAlreadyExistException extends Exception{


    public AccountAlreadyExistException (String message){
        super(message);
    }
    // Constructor with message and cause
    public AccountAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
