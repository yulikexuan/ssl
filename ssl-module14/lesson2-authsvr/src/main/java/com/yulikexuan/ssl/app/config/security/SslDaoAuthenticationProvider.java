//: com.yulikexuan.ssl.app.config.security.SslDaoAuthenticationProvider.java


package com.yulikexuan.ssl.app.config.security;


import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.services.IUserService;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;


public class SslDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private final IUserService userService;

    public SslDaoAuthenticationProvider(IUserService userService) {
        super();
        this.userService = userService;
    }

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        super.additionalAuthenticationChecks(userDetails, authentication);

        String verificationCode =
                ((SslWebAuthenticationDetails) authentication.getDetails())
                        .getVerificationCode();

        System.out.printf("Verification code from request parameter: %1$s",
                verificationCode);

        String username = authentication.getName();

        if (!"admin".equals(username)) {
            final Optional<User> userOpt = this.userService.findUserByUsername(
                    username);

            String secret = userOpt.map(User::getSecret)
                    .orElse("");

            final Totp totp = new Totp(secret);

            try {
                if (!totp.verify(verificationCode)) {
                    throw new BadCredentialsException("Invalid verification code!");
                }
            } catch (final Exception e) {
                throw new BadCredentialsException("Invalid verification code!");
            }
        }

    }

}///:~