//: com.yulikexuan.ssl.app.config.security.SslSecurityConfigerAdapter.java


package com.yulikexuan.ssl.app.config.security;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;


@Slf4j
@Configuration
@EnableWebSecurity
public class SslSecurityConfigerAdapter extends WebSecurityConfigurerAdapter {

    public static final String DEFAULT_SIMPLE_PW = "123456";

    private final UserDetailsService userDetailsService;

    @Value("${app.security.permit.urls}")
    private String[] permitUrls;

    @Autowired
    public SslSecurityConfigerAdapter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {

        ProviderManager authenticationManager = new ProviderManager(
                Lists.newArrayList(this.daoAuthenticationProvider()));

        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return SslPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authManagerBuilder)
            throws Exception {
        authManagerBuilder.parentAuthenticationManager(
                this.authenticationManager());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .accessDecisionManager(this.accessDecisionManager())
                .antMatchers("/delete/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECURITY") // "ROLE_" prefix is required here

                //: URL Authorization with Expressions
                .antMatchers("/secured*")
                //.access("hasRole('ROLE_ADMIN') or principal.username =='admin'")
                .access("hasAuthority('PRIVILEGE_SECURITY')")
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

    }//  End of configure(HttpSecurity)

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {

        final DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> voters =
                List.of(new WebExpressionVoter(), new RoleVoter(),
                        new AuthenticatedVoter(), PrivilegeVoter.create());
        return new AffirmativeBased(voters);
    }

}///:~