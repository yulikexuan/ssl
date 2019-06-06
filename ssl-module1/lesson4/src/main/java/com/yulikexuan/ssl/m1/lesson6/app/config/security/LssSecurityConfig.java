//: com.yulikexuan.ssl.m1.lesson6.app.config.security.LssSecurityConfig.java


package com.yulikexuan.ssl.m1.lesson6.app.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class LssSecurityConfig extends WebSecurityConfigurerAdapter {

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
                .anyRequest()
                .authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .loginProcessingUrl("/whoareyou");

    }// @formatter:off

}///:~