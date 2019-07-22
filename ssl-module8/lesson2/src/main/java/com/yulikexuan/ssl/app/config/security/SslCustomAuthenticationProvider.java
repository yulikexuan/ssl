//: com.yulikexuan.ssl.app.config.security.SslCustomAuthenticationProvider.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


/*
 * Spring Security provides a variety of options for performing authentication
 * These follow a simple contract
 *   – An Authentication request is processed by an AuthenticationProvider
 *   - A fully authenticated object with full credentials is returned
 *
 * The standard and most common implementation is the DaoAuthenticationProvider
 *   – Which retrieves the user details from a simple, read-only user DAO:
 *     the UserDetailsService
 *   - This User Details Service only has access to the username in order to
 *     retrieve the full user entity
 *
 * More custom scenarios will still need to access the full Authentication
 * request to be able to perform the authentication process
 * For example, when authenticating against some external, third party service
 * (such as Crowd) – both the username and the password from the authentication
 * request will be necessary
 * For these, more advanced scenarios, we’ll need to define a custom
 * Authentication Provider
 */
@Component
public class SslCustomAuthenticationProvider implements AuthenticationProvider {

    /*
     * Performs authentication with the same contract as
     * AuthenticationManager.authenticate(Authentication):
     * Attempts to authenticate the passed Authentication object,
     * returning a fully populated Authentication object (including granted
     * authorities) if successful.
     *
     * An AuthenticationManager must honour the following contract concerning
     * exceptions:
     *   - A DisabledException must be thrown if an account is disabled and the
     *     AuthenticationManager can test for this state
     *   - A LockedException must be thrown if an account is locked and the
     *     AuthenticationManager can test for account locking
     *   - A BadCredentialsException must be thrown if incorrect credentials are
     *     presented.
     * Whilst the above exceptions are optional, an AuthenticationManager must
     * always test credentials
     *
     * Exceptions should be tested for and if applicable thrown in the order
     * expressed above (i.e. if an account is disabled or locked,
     * the authentication request is immediately rejected and the credentials
     * testing process is not performed)
     *
     * This prevents credentials being tested against disabled or locked
     * accounts
     *
     * Parameters:
     *   authentication - the authentication request object
     */
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();

        if (!this.supportsAuthentication(authentication)) {
            return null;
        }

        // Use the credentials and authenticate against the third-party system
        if (this.shouldAuthenticateAgainstThirtyPartySystem(authentication)) {
            return new UsernamePasswordAuthenticationToken(name, password,
                    new ArrayList<>());
        } else {
            throw new BadCredentialsException(
                    "Authentication against the third party system failed!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    private boolean shouldAuthenticateAgainstThirtyPartySystem(
            Authentication authentication) {

        // Execute the third party authentication ... ...
        return false;
    }

    private boolean supportsAuthentication(Authentication authentication) {
        return true;
    }

}///:~