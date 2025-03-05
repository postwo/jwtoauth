package com.example.jwtoauthtoken.exception;

import org.springframework.http.HttpStatus;

public class ClientErrorException extends RuntimeException { //400번대 에러

    private final HttpStatus status;

    public ClientErrorException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}