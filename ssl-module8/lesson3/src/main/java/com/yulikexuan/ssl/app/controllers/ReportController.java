//: com.yulikexuan.ssl.app.controllers.ReportController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.domain.services.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
@RequestMapping("/report")
public class ReportController {

    private final ReportService runAsService;

    @Autowired
    public ReportController(ReportService runAsService) {
        this.runAsService = runAsService;
    }

    /*
     * "RUN_AS_REPORTER" ConfigAttribute starts with "RUN_AS_"
     * "RUN_AS_" ConfigAttribute is used to create a new GrantedAuthorityImpl by
     * a RunAsManager implemention
     * This new GrantedAuthorityImpl will be prefixed with ROLE_, followed by
     * the RUN_AS ConfigAttribute: "ROLE_RUN_AS_REPORTER"
     *
     * The replacement RunAsUserToken returned by RunAsManager will contain the
     * same principal, credentials and granted authorities as the original
     * Authentication object, along with a new GrantedAuthorityImpl for each
     * "RUN_AS_" ConfigAttribute
     *
     * Must have a non-run-as configAttribute also, like "ROLE_USER" here;
     * That means who runs as a reporter
     */
    @RequestMapping
    @ResponseBody
    @Secured({"ROLE_USER", "RUN_AS_REPORTER"})
    public String tryRunAs() {

        final Authentication authentication = this.runAsService.getCurrentUser();

        authentication.getAuthorities().forEach(
                auth -> log.info(">>>>>>> Run-As Authorities: {}",
                        auth.getAuthority()));

        return "Current User Authorities inside this RunAS method only " +
                authentication.getAuthorities().toString();
    }

}///:~