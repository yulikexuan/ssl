//: com.yulikexuan.ssl.app.config.security.LssSecurityConfig.java


package com.yulikexuan.ssl.app.config.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;


@Slf4j
@Configuration
@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

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

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().
                csrf()
                .disable();
    }

}///:~