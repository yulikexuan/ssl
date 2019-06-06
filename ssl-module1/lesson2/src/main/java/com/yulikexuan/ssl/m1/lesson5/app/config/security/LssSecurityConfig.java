//: com.yulikexuan.ssl.m1.lesson5.app.config.security.LssSecurityConfig.java


package com.yulikexuan.ssl.m1.lesson5.app.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authManagerBuilder)
            throws Exception {

        // @formatter:off

        /*
         * 1.  Disable the basic authentication
         * 2.  Replace with form authentication configuration
         * 3.  Spring will auto-generate a form for authentication
         * 4.  Add password storage format, for plain text, add {noop}
         *     - Prior to Spring Security 5.0 the default PasswordEncoder was
         *       NoOpPasswordEncoder which required plain text passwords
         *     - In Spring Security 5, the default is DelegatingPasswordEncoder,
         *       which required Password Storage Format
         */
        authManagerBuilder.inMemoryAuthentication()
                .withUser("yul")
                .password("{noop}123456")
                .roles("USER");

    } // @formatter:on

}///:~