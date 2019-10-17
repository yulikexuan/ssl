# Setup OAuth2 Resource Server with Spring Security


## How to set up an authorization server

1.  Create Resource Server Configuration class which extends:
    ResourceServerConfigurerAdapter class
    
    ``` 
    public class ResourceServerConfigurer extends ResourceServerConfigurerAdapter {
    
    }
    ```

2.  Add annotations:

    ``` 
    @Configuration
    @EnableResourceServer
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(securedEnabled = true)
    public class ResourceServerConfigurer extends ResourceServerConfigurerAdapter {
    
    }
    ```

3.  Config HTTP Security 

    ``` 
    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/res/welcome")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable();
    }
    ```

4.  Add security key, which is exactly same as the one used by OAuth2 
    Authorization Server, to ```application.yml```

    ``` 
    server:
      port: 8089
    security:
      oauth2:
        resource:
          jwt:
            key-value: oui214hmui23o4hm1pui3o2hp4m1o3h2m1o43
    ```
