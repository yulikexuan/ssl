//: com.yulikexuan.ssl.app.config.security.LssSecurityConfig.java


package com.yulikexuan.ssl.app.config.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Slf4j
@Configuration
@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String DEFAULT_SIMPLE_PW = "123456";

    private final UserDetailsService userDetailsService;

    @Autowired
    public LssSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /*
     * @Autowired for config methods
     *   - Config methods may have an arbitrary name and any number of arguments;
     *   - Each of those arguments will be autowired with a matching bean in the
     *     Spring container
     *   - Bean property setter methods are effectively just a special case of
     *     such a general config method
     *   - Such config methods do not have to be public
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authManagerBuilder)
            throws Exception {

        // @formatter:off

        /* Module 1:
         * 1.  Disable the basic authentication
         * 2.  Replace with form authentication configuration
         * 3.  Spring will auto-generate a form for authentication
         * 4.  Add password storage format, for plain text, add {noop}
         *     - Prior to Spring Security 5.0 the default PasswordEncoder was
         *       NoOpPasswordEncoder which required plain text passwords
         *     - In Spring Security 5, the default is DelegatingPasswordEncoder,
         *       which required Password Storage Format
         */
//        authManagerBuilder.inMemoryAuthentication()
//                .withUser("yul")
//                .password("{noop}123456")
//                .roles("USER");


        /*
         * Module 2:
         *
         */
        authManagerBuilder.userDetailsService(this.userDetailsService);

    } // @formatter:on

    /*
     * The default configuration is:
     *   http.authorizeRequests() //Allows restricting access based upon the HttpServletRequest using
     *       .anyRequest().authenticated()
     *       .and()
     *       .formLogin() //Specifies to support form based authentication.
     *                    // If FormLoginConfigurer.loginPage(String) is not
     *                    // specified a default login page will be generated.
     *       .and().httpBasic();
     * HttpSecurity:
     *   - It allows configuring web based security for specific http requests
     *   - By default it will be applied to all requests, but can be restricted
     *     using requestMatcher(RequestMatcher) or other similar methods
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // @formatter:off

        // Always authorize urls before authenticating request
        http
            .authorizeRequests()
                .antMatchers("/delete/**")
                // .hasRole("ADMIN") // "ROLE_" prefix will be auto-inserted
                // .hasAnyRole("ADMIN", "ADMIN2");
                // .hasAnyAuthority("ROLE_ADMIN", "ROLE_ADMIN2");
                .hasAuthority("ROLE_ADMIN")
                .antMatchers("/signup", "/user/register", "/registrationConfirm*/**", "/h2-console/*/**")
                .permitAll()
                .anyRequest()
                .authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .loginProcessingUrl("/whoareyou")
            .and()
                .logout()
                .permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher(
                        "/quit", "GET"))// POST is the better
                .clearAuthentication(true)// Default action
                .invalidateHttpSession(true)// Default
            .and() // Disable X-Frame-Options in Spring Security
                .headers() // So we can user the console of h2 database
                .frameOptions()
                .disable()
            .and()
                .csrf()
                .disable();

            /*
             * Adding CSRF will update the LogoutFilter to only use HTTP POST
             * This ensures that log out requires a CSRF token and that a
             * malicious user cannot forcibly log out your users
             * See: https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/csrf.html
             *      19.5.3: Logging Out
             */

    }// @formatter:off


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}///:~