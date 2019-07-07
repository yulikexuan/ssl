//: com.yulikexuan.ssl.app.config.security.SslSecurityExpressionOperation.java


package com.yulikexuan.ssl.app.config.security;


import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;


/*
 * MethodSecurityExpressionOperations:
 *   - Interface which must be implemented if you want to use filtering in
 *     method security expressions
 */
public class SslSecurityExpressionOperation extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;

    public SslSecurityExpressionOperation(Authentication authentication) {
        super(authentication);
    }

    public boolean isAdmin() {
        return Optional.ofNullable(this.getPrincipal())
                .map(this::isUserHasAdminRole)
                .orElse(false);
    }

    private boolean isUserHasAdminRole(Object o) {

        if ((o == null) || !(o instanceof User)) {
            return false;
        }

        final User user = (User)o;
        return user.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN"));
    }


    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    void setThis(Object target) {
        this.target = target;
    }

}///:~