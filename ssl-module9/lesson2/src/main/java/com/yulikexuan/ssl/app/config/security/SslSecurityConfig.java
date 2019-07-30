//: com.yulikexuan.ssl.app.config.security.SslSecurityConfig.java


package com.yulikexuan.ssl.app.config.security;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.stream.Collectors;


/*
 * This configuration class is generic for all lessons except Lesson 2 to 4 of Module 5
 */
@Slf4j
@Configuration
@EnableAsync
@EnableWebSecurity
public class SslSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String DEFAULT_SIMPLE_PW = "123456";

    private final UserDetailsService userDetailsService;

    @Value("${app.security.permit.urls}")
    private String[] permitUrls;

    @Autowired
    public SslSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /*
     * In most cases, we do not have to create new AuthenticationManager
     * And, setEraseCredentialsAfterAuthentication make high security risk but
     * it better for cache SecurityContext
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {

        ProviderManager authenticationManager = new ProviderManager(
                Lists.newArrayList(this.daoAuthenticationProvider()));

        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return SslPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /*
     * @Autowired for config methods
     *   - Config methods may have an arbitrary name and any number of arguments;
     *   - Each of those arguments will be autowired with a matching bean in the
     *     Spring container
     *   - Bean property setter methods are effectively just a special case of
     *     such a general config method
     *   - Such config methods do not have to be public
     *
     * Autowired AuthenticationManagerBuilder instance is from
     * AuthenticationConfiguration which exports the authentication Configuration
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authManagerBuilder)
            throws Exception {
        authManagerBuilder.parentAuthenticationManager(
                this.authenticationManager());
    }

    /*
     * The default configuration is:
     *   http.authorizeRequests() //Allows restricting access based upon the HttpServletRequest using
     *       .anyRequest()
     *       .authenticated()
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

        log.info(">>>>>>> app.security.permit.urls: {}",
                Arrays.stream(permitUrls).collect(Collectors.joining(", ")));

        // Always authorize urls before authenticating request
        // User roles are setup in LssUserDetailsService class !
        http.authorizeRequests()
                .antMatchers("/delete/**")
                // .hasRole("ADMIN") // "ROLE_" prefix will be auto-inserted
                // .hasAnyRole("ADMIN", "ADMIN2");
                // .hasAnyAuthority("ROLE_ADMIN", "ROLE_ADMIN2");
                .hasAuthority("ROLE_ADMIN") // "ROLE_" prefix is required here

                //: URL Authorization with Expressions
                .antMatchers("/secured*")
                /*
                 * Allows specifying that URLs are secured by an arbitrary
                 * expression
                 * The expression to secure the URLs (i.e. "hasRole('ROLE_USER')
                 * and hasRole('ROLE_SUPER')")
                 */
                // .access("hasRole('ADMIN')") // "ROLE_" prefix is not required here
                // .access("hasAuthority('ROLE_ADMIN')") // "ROLE_" prefix is required here
                // .hasIpAddress("192.168.1.79/24")
                /*
                 * This is because ping on Windows Vista and newer Windows uses
                 * IPv6 by default when available.
                 * ::1 is a shortened notation of IPv6 loopback address
                 *   - equivalent of IPv4 loopback 127.0.0.1.
                 *
                 * The full notation of the abbreviated ::1 IPv6 address is
                 * 0000:0000:0000:0000:0000:0000:0000:0001.
                 *
                 * If you want to force ping to use IPv4 instead you can specify
                 * the IPv4 address explicitly or use the -4 option.
                 * ping 127.0.0.1
                 * ping -4 localhost
                 */
                // .not().access("hasIpAddress('::1')")
                // .anonymous()
                // .access("isAnonymous()")
                // .access("request.method == 'GET'")
                //.access("request.method != 'POST'")
                // .access("hasRole('ROLE_USER') and principal.username =='yul'")
                .access("hasRole('ROLE_ADMIN') or principal.username =='admin'")
                //: End of Authorization Expression

                .antMatchers(permitUrls)
                .permitAll()

                .anyRequest()
                .authenticated()

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

    }//  End of configure(HttpSecurity)

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {

        final DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());

        return daoAuthenticationProvider;
    }

}///:~
