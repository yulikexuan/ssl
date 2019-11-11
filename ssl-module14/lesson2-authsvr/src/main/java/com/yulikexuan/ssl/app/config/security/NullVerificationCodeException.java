//: com.yulikexuan.ssl.app.config.security.NullVerificationCodeException.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.security.core.AuthenticationException;


public class NullVerificationCodeException extends AuthenticationException {

    private final String username;

    public NullVerificationCodeException(String username) {
        super(username);
        this.username = username;
    }

    public NullVerificationCodeException(String username, Throwable t) {
        super(username, t);
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

}///:~