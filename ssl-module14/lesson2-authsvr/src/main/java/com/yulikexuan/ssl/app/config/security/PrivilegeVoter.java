//: com.yulikexuan.ssl.app.config.security.PrivilegeVoter.java


package com.yulikexuan.ssl.app.config.security;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


@Getter
@Setter
public class PrivilegeVoter implements AccessDecisionVoter<Object> {

    private String privilegePrefix = "PRIVILEGE_";

    private PrivilegeVoter() {}

    public static PrivilegeVoter create() {
        return new PrivilegeVoter();
    }

    public static PrivilegeVoter create(String privilegePrefix) {
        PrivilegeVoter voter = new PrivilegeVoter();
        voter.setPrivilegePrefix(privilegePrefix);
        return voter;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return Optional.ofNullable(attribute)
                .map(ConfigAttribute::getAttribute)
                .filter(att -> att.startsWith(
                        this.getPrivilegePrefix()))
                .isPresent();
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object,
                    Collection<ConfigAttribute> attributes) {

        if ((authentication == null) || (attributes == null)) {
            return ACCESS_DENIED;
        }

        if (attributes.size() == 0) {
            return ACCESS_ABSTAIN;
        }

        return attributes.stream()
                .filter(this::supports)
                .map(ConfigAttribute::getAttribute)
                .anyMatch(authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                        ::contains) ? ACCESS_GRANTED : ACCESS_DENIED;
    }

}///:~