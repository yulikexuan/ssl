//: com.yulikexuan.ssl.domain.services.SslActiveUserService.java


package com.yulikexuan.ssl.domain.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SslActiveUserService {

    private final SessionRegistry sessionRegistry;

    @Autowired
    public SslActiveUserService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public List<String> getAllActiveUsers() {
        return this.sessionRegistry.getAllPrincipals().stream()
                .map(p -> (User)p)
                .filter(user -> !this.sessionRegistry.getAllSessions(user,
                        false).isEmpty())
                .map(user -> user.getUsername())
                .collect(Collectors.toList());
    }

}///:~