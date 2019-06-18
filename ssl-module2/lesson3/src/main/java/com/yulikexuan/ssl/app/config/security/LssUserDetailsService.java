//: com.yulikexuan.ssl.app.config.security.LssUserDetailsService.java


package com.yulikexuan.ssl.app.config.security;


import com.yulikexuan.ssl.domain.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Primary
@Service
public class LssUserDetailsService implements UserDetailsService {

    static final String ROLE_USER = "ROLE_USER";

    private final IUserService userService;

    @Autowired
    public LssUserDetailsService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return this.userService.findUserByUsername(username)
                .map(LssUserDetailsService::userToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with username: " + username));
    }

    public static UserDetails userToUserDetails(
            com.yulikexuan.ssl.domain.model.User lssUser) {

        return User.builder()
                .username(lssUser.getUsername())
                .password(lssUser.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .authorities(new SimpleGrantedAuthority(ROLE_USER))
                .build();
    }

}///:~
