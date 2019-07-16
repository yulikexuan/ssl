//: com.yulikexuan.ssl.domain.services.ReportService.java


package com.yulikexuan.ssl.domain.services;


import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class ReportService {

    /*
     * The Secured annotation is used to define a list of security configuration
     * attributes for business methods
     *
     * "ROLE_RUN_AS_REPORTER" is a "RUN_AS_" ConfigAttribute
     *
     * "RUN_AS_REPORTER" is the ConfigAttribute used by the controller which
     * will call this service method. Here in this project, the controller is
     * ReportController
     *
     * It is the RunAsManager who adds "ROLE_" // To be made sure
     */
    @Secured("ROLE_RUN_AS_REPORTER")
    public Authentication getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        return authentication;
    }

}///:~