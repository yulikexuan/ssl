# Setup OAuth2 with Spring Security


## How to set up an authorization server

1.  Set up Authentication 

    a. Create a class which extends ```WebSecurityConfigurerAdapter```
           
    b. Expose ``` AuthenticationManager ``` from ``` LssSecurityConfig ```
    
    c. Config ``` AuthenticationManagerBuilder ``` to use ```inMemoryAuthentication```

    d. Set up in-memory username, password and roles
    
    e. Config HTTP security for URL patterns
    
    f. Add annotations
    
       ``` 
        // f.:
        @Slf4j
        @Configuration
        @EnableWebSecurity
        // a.:
        public class LssSecurityConfig extends WebSecurityConfigurerAdapter { 
        
            // b.:
            @Bean
            @Override
            public AuthenticationManager authenticationManagerBean() throws Exception {
                return super.authenticationManagerBean();
            }
        
            // c.:
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
                authManagerBuilder.inMemoryAuthentication() // d.: 
                        .withUser("yul")
                        .password("{noop}123456")
                        .roles("USER");
        
            } // @formatter:on
        
            @Override
            public void configure(final HttpSecurity http) throws Exception {
                // e.: 
                http.authorizeRequests()
                        .anyRequest()
                        .authenticated()
                        .and()
                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                        .csrf()
                        .disable();
            }
        
        }///:~
       ```
    
2.  Setup Authorization Server

    a. Create a class which extends ``` AuthorizationServerConfigurerAdapter ```

    b. Add annotations to the Authorization Server Configurer class
       - @Configuration
       - @EnableAuthorizationServer
       
    c. Autowire the ``` AuthenticationManager ``` instance
    
    d. Define a simple in-memory  ``` TokenStore ```

    e. Set up ``` AuthorizationServerEndpointsConfigurer ```
       - Set up ``` TokenStore ```
       - Set up ``` AuthenticationManager ```
    
    f. Set up Authorization Client
       - Set up one client in memory with client name and secret
       - Set up authorized grant types: using passoword flow here 
       - Define and auto approve a default scope
       - Specify a token validity in seconds

    ```
    // a. & b.
    @Slf4j
    @Configuration
    @EnableAuthorizationServer
    public class AuthorizationServerConfiguration
            extends AuthorizationServerConfigurerAdapter {
    
        private final AuthenticationManager authenticationManager;
    
        // c.:
        @Autowired
        public AuthorizationServerConfiguration(
                AuthenticationManager authenticationManager) {
    
            super();
    
            this.authenticationManager = authenticationManager;
        }
    
        // f.: 
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    
            clients.inMemory()
                    .withClient("client")
                    .secret("{noop}123456")
                    .authorizedGrantTypes("password")
                    .scopes("resources")
                    .autoApprove("resources")
                    .accessTokenValiditySeconds(3600);
        }
    
        // e.: 
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {
    
            endpoints.tokenStore(tokenStore()).
                    authenticationManager(this.authenticationManager).
                    allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
        }
    
        // d.:
        @Bean
        public TokenStore tokenStore() {
            return new InMemoryTokenStore();
        }
    
    }///:~
    ```

3.  How to test

    a. Url for request a token:
       ``` http://localhost:8081/resources/oauth/token ```
       Server configuration: 
       ``` 
       server:
         servlet:
           context-path: /resources
         port: 8081 
       ```
       
    b. Parameters:
       - grant_type=password
       - client_id=client
       - username=yul
       - password=123456
       
    c. Authorization Header:
       - Username=client // The client id
       - Password=123456 // The client secret
       
    d. Full Url: 
       ``` http://localhost:8081/resources/oauth/token?grant_type=password&client_id=client&username=yul&password=123456 ```
    
    e. The request http method should be: ```POST``` other than ```GET```


## How to set up JWT Token with Authoriation Server

1.  Make authorization server issue JWT tokens

    a.  Issue a Bean of JwtAccessTokenConverter as a Jwt Token Enhancer
    
        ``` 
        @Value("${signing-key:oui214hmui23o4hm1pui3o2hp4m1o3h2m1o43}")
        private String signingKey;
        
        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
    
            final JwtAccessTokenConverter jwtAccessTokenConverter =
                    new JwtAccessTokenConverter();
    
            jwtAccessTokenConverter.setSigningKey(signingKey);
    
            return jwtAccessTokenConverter;
        }
        ```
        
        **This access token converter should be exactly same as the one in the 
        Resource Server**

    b.  Use ``` tokenStore ``` method to issue a Bean of TokenStore
    
        ``` 
        @Bean
        public TokenStore tokenStore() {
            return new JwtTokenStore(accessTokenConverter());
        }
        ```
        
    c.  Set access token converter up to AuthorizationServerEndpointsConfigurer 
    
        ``` 
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {
    
            endpoints.tokenStore(tokenStore())
                    .authenticationManager(this.authenticationManager)
                    .accessTokenConverter(this.accessTokenConverter())
                    .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
        }
        ```

## How to set up refreshable JWT Token with Authoriation Server

1.  Customize ```ClientDetailsServiceConfigurer```

    a.  Add a new grant type **"refresh_token"**
    
    b.  Set up validity seconds to refresh token

        ``` 
        clients.inMemory()
                .withClient("client")
                .secret("{noop}123456")
                .authorizedGrantTypes("password", "refresh_token") // a.
                .refreshTokenValiditySeconds(3600 * 24) // One Day // b.
                .scopes("resources", "read", "write", "trust")
                .autoApprove("resources")
                .accessTokenValiditySeconds(3600); // One Hour
        ```

2.  Customize and issue a new ```TokenServices``` Bean

    a.  Create a new method to issue ```DefaultTokenServices``` Bean
    
    b.  Set Jwt token store up to ```DefaultTokenServices``` Bean
    
    c.  Set ```SupportRefreshToken``` feature to be ```true```
    
    d.  Make the ```DefaultTokenServices``` Bean be ```primary``` in order to
        override the default one that spring security supports
    
    ``` 
    @Bean
    @Primary // d.
    public DefaultTokenServices tokenServices() { // a.

        DefaultTokenServices tokenServices = new DefaultTokenServices();

        tokenServices.setTokenStore(this.tokenStore()); // b.
        tokenServices.setSupportRefreshToken(true); // c.

        return tokenServices;
    }
    ```

3.  Wiring in our UserDetailsService

    a.  Declare a field of ```UserDetailsService```
    
    b.  Set the ```UserDetailsService``` field up to ```AuthorizationServerEndpointsConfigurer```

    ``` 
    private final UserDetailsService userDetailsService; // a. 
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {

        endpoints.tokenStore(tokenStore())
                .authenticationManager(this.authenticationManager)
                .userDetailsService(this.userDetailsService) // b.
                .accessTokenConverter(this.accessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }
    ```

4.  Set up checking Jwt tokens

    a.  Override the base method and customize ``` AuthorizationServerSecurityConfigurer ```
    
    b.  Opening up the access to the endpoint for checking
    
        - Can always pass a more restrictive type of expression here
    
        ``` 
        // a. 
        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.checkTokenAccess("permitAll()"); // b.
            super.configure(security);
        }
        ```

5.  How to test

    a.  The authorization header:
    
        - ``` Basic Auth ``` Type
        - Username: client [the client id]
        - Password: 123456 [the secret]

    b.  The URI to get a refreshable Jwt Token:
    
        ``` http://localhost:8083/resources/oauth/token?grant_type=password&client_id=client&username=yul&password=123456 ```

        - In the response, there is not only a Jwt token but also a refresh token
        
    c.  The URI to refresh a Jwt Token:
    
        ``` http://localhost:8083/resources/oauth/token?grant_type=refresh_token&refresh_token={the refresh token} ```

        - Parameters:
          - grant_type=refresh_token
          - refresh_token={The refresh token}

    d.  The URI for checking Jwt Tokens
    
        ``` http://localhost:8083/resources/oauth/check_token ```
        
        - Paremeter:
          - token={the token to be checked}