//: com.yulikexuan.ssl.app.config.security.SslWebAuthenticationDetails.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


public class SslWebAuthenticationDetails extends WebAuthenticationDetails {

    private String verificationCode;

    public SslWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.verificationCode = request.getParameter("verificationCode");
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public Optional<String> getVerificationCodeOpt() {
        return Optional.ofNullable(this.verificationCode);
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

}///:~