//: com.yulikexuan.ssl.app.config.security.LssUserDetailsService.java


package com.yulikexuan.ssl.app.services;


import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.yulikexuan.ssl.domain.model.Role;
import com.yulikexuan.ssl.domain.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Primary
@Service
public class SslUserDetailsService implements UserDetailsService {

    static final String ROLE_USER = "ROLE_USER";
    static final String ROLE_ADMIN = "ROLE_ADMIN";

    static final List<GrantedAuthority> DEFAULT_GRANTED_AUTHORITIES =
            ImmutableList.of(new SimpleGrantedAuthority(ROLE_USER));

    static final List<GrantedAuthority> ADMIN_GRANTED_AUTHORITIES =
            ImmutableList.of(
                    new SimpleGrantedAuthority(ROLE_USER),
                    new SimpleGrantedAuthority(ROLE_ADMIN));

    private final IUserService userService;

    @Autowired
    public SslUserDetailsService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return this.userService.findUserByUsername(username)
                .map(this::userToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with username: " + username));
    }

    private UserDetails userToUserDetails(
            com.yulikexuan.ssl.domain.model.User lssUser) {

        GrantedAuthority[] authorities = {};

        return User.builder()
                .username(lssUser.getUsername())
                .password(lssUser.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .disabled(!lssUser.getEnabled())
                .credentialsExpired(false)
                .authorities(this.getAuthorities(lssUser.getRoles()))
                .build();
    }

    public final Collection<? extends GrantedAuthority> getAuthorities(
            final Collection<Role> roles) {

        assert roles != null : "Roles should not be null.";

        Collection<SimpleGrantedAuthority> authorities = roles.stream()
                .flatMap(role -> role.getAllAuthorityNames().stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return ImmutableList.copyOf(authorities);
    }

}///:~
