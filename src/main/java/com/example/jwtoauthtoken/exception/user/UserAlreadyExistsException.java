package com.example.jwtoauthtoken.exception.user;


import com.example.jwtoauthtoken.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ClientErrorException {

    // 이미 회원가입을 한 사용자에게 반환
    public UserAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "User already exists");
    }

    public UserAlreadyExistsException(String username) {
        super(HttpStatus.CONFLICT, "User with username"+ username +"already exists");
    }

}
