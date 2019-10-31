### Add Maven Dependencies to POM

    ``` 
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.security.oauth.boot</groupId>
            <artifactId>spring-security-oauth2-autoconfigure</artifactId>
            <version>2.1.8.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <dependency>
            <groupId>org.thymeleaf.extras</groupId>
            <artifactId>thymeleaf-extras-springsecurity5</artifactId>
        </dependency>
    ```
### Add ```@EnableOAuth2Sso``` to ```SslSecurityConfigerAdapter```

### Defined a ```RequestContextListener``` bean to handle requests scopes

### Security Configuration

1.  Use Form Login

    ``` 
    @Configuration
    @EnableOAuth2Sso
    @Order(value = 0)
    public class SslSecurityConfigerAdapter extends WebSecurityConfigurerAdapter {
    
        @Override
        public void configure(HttpSecurity http) throws Exception {
    
            http.authorizeRequests()
                    .antMatchers("/", "/login")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .formLogin()
                    .permitAll()
                    .and()
                    .logout()
                    .logoutSuccessUrl("http://localhost:8081/exit")
                    .and()
                    .csrf()
                    .disable();
        }

    }///:~
    ```

### Config Application.yml

1.  Config OAuth2 Client

2.  Config JWT Token

3.  Config user info uri to obtain current user details

    ``` 
    server:
      port: 8082
      servlet:
        context-path: /dms
        
    spring:
      thymeleaf:
        cache: false
    
    security:
      oauth2:
        sso:
          loginPage: /login
        client:
          clientId: dms
          clientSecret: 2PGlgRk9Mv
          accessTokenUri: http://localhost:8081/oauth/token
          userAuthorizationUri: http://localhost:8081/oauth/authorize
          clientAuthenticationScheme: form
        resource:
          userInfoUri: http://localhost:8081/api/users/me
          jwt:
            keyUri: http://localhost:8081/oauth/token
            keyValue: 6264BB136A72A461C3ACCFB2FC1BF
    
    logging:
      level:
        org:
          apache:
            tomcat: INFO
            catalina: INFO
    ```



### Resources:
- [Simple Single Sign-On with Spring Security OAuth2](https://www.baeldung.com/sso-spring-security-oauth2)
- [Configuring RequestContextListener in SpringBoot](https://stackoverflow.com/questions/30254079/configuring-requestcontextlistener-in-springboot)