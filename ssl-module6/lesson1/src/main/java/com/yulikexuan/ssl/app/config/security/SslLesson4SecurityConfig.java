//: com.yulikexuan.ssl.app.config.security.SslLesson4SecurityConfig.java


package com.yulikexuan.ssl.app.config.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/*
 * This configuration class is only for lesson 2 to 4 of Module 5
 */
@Slf4j
//@Configuration
//@EnableWebSecurity
public class SslLesson4SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Value("${app.security.permit.urls}")
    private String[] permitUrls;

    @Autowired
    public SslLesson4SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /*
     * Override this method to expose the AuthenticationManager from
     * configure(AuthenticationManagerBuilder) to be exposed as a Bean
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authManagerBuilder)
            throws Exception {

        // Module 2:
        authManagerBuilder.userDetailsService(this.userDetailsService);

    } // @formatter:on

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                    .anyRequest()
                    .permitAll()
                .and()
                    .formLogin()
                    .loginPage("/login") // Specifies the URL to send users to if login is required
                    .loginProcessingUrl("/whoareyou") // Specifies the URL to validate the credentials
                    .permitAll() // Ensures the urls for the getLoginPage() and getLoginProcessingUrl() are granted access to any user.
                .and()
                .logout()
                    .permitAll()
                    .logoutRequestMatcher(new AntPathRequestMatcher(
                            "/quit", "GET"))// POST is the better
                    .clearAuthentication(true)// Default action
                    .invalidateHttpSession(true)// Default action
                .and()
                    .rememberMe()
                    .tokenValiditySeconds(7 * 24 * 3600)
                    .key("lssappkey")
                    // .useSecureCookie(true)
                    .rememberMeCookieName("sticky-cookie")
                    .rememberMeParameter("sticky") // Used to name the check-box on login page
                .and() // Disable X-Frame-Options in Spring Security
                .headers() // So we can user the console of h2 database
                    .frameOptions()
                    .disable()
                .and()
                    .csrf()
                    .disable();
    }

}///:~