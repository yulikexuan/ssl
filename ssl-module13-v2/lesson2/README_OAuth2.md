# Setup OAuth2 with Spring Security


## How to set up an authorization server

1.  Set up Authentication 

    a. Create a class which extends ```WebSecurityConfigurerAdapter```
           
    b. Expose ``` AuthenticationManager ``` from ``` LssSecurityConfig ```
       also includes:
       - ``` PasswordEncoder ```
       - ``` daoAuthenticationProvider ```
       - ``` accessDecisionManager ```
    
    c. Config ``` AuthenticationManagerBuilder ``` to use ```inMemoryAuthentication```

    d. Set up users, roles and privileges
    
    e. Config HTTP security for URL patterns
    
    f. Add annotations
    
       ``` 
        @Slf4j
        @Configuration
        @EnableWebSecurity
        public class SslSecurityConfigerAdapter extends WebSecurityConfigurerAdapter {
        
            public static final String DEFAULT_SIMPLE_PW = "123456";
        
            private final UserDetailsService userDetailsService;
        
            @Value("${app.security.permit.urls}")
            private String[] permitUrls; // "/h2-console/**"
        
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
                        .antMatchers(this.permitUrls)
                        .permitAll()
        
                        .anyRequest()
                        .authenticated()
        
                        .and()
                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        
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
       ```
    
2.  Setup Authorization Server

    a. Create a class which extends ``` AuthorizationServerConfigurerAdapter ```

    b. Add annotations to the Authorization Server Configurer class
       - @Configuration
       - @EnableAuthorizationServer
       
    c. Autowire the ``` AuthenticationManager ``` instance
    
    d. Autowire the ``` ClientDetailsService ```
    
    e. Autowire the ``` PasswordEncoder ```
    
    f. Define a ``` JwtAccessTokenConverter ``` Bean
    
    g. Define a ``` JwtTokenStore ``` Bean with the ``` JwtAccessTokenConverter ``` Bean 

    e. Set up ``` AuthorizationServerEndpointsConfigurer ```
       - Set up ``` TokenStore ``` 
       - Set up ``` JwtAccessTokenConverter ```
       - Set up ``` AuthenticationManager ```
    
    f.a Set up Authorization Client in Memory
       - Set up one client in memory with client name and secret
       - Set up authorized grant types: using passoword flow here 
       - Define and auto approve a default scope
       - Specify a token validity in seconds

    f.b Set up Authorization Client with client in Database
       
    ```
    // a. & b.
    @Configuration
    @EnableAuthorizationServer
    public class SslAuthorizationServerConfiguration extends
            AuthorizationServerConfigurerAdapter {
    
        private final AuthenticationManager authenticationManager;
        private final PasswordEncoder passwordEncoder;
        private final ClientDetailsService clientDetailsService;
    
        @Value("${app.security.oauth2.jwt.signingkey}")
        private String signingKey = "6264BB136A72A461C3ACCFB2FC1BF";
    
        @Autowired
        public SslAuthorizationServerConfiguration(
                AuthenticationManager authenticationManager,
                PasswordEncoder passwordEncoder,
                ClientDetailsService clientDetailsService) {
    
            this.authenticationManager = authenticationManager;
            this.passwordEncoder = passwordEncoder;
            this.clientDetailsService = clientDetailsService;
        }
    
        // Beans:
    
        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
            final JwtAccessTokenConverter accessTokenConverter =
                    new JwtAccessTokenConverter();
            accessTokenConverter.setSigningKey(signingKey);
            return accessTokenConverter;
        }
    
        @Bean
        public TokenStore tokenStore() {
            return new JwtTokenStore(this.accessTokenConverter());
        }
    
        // Configuration:
    
        @Override
        public void configure(final AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {
    
            endpoints.tokenStore(this.tokenStore())
                    .authenticationManager(this.authenticationManager)
                    .accessTokenConverter(this.accessTokenConverter())
                    .allowedTokenEndpointRequestMethods(HttpMethod.POST);
        }
    
        @Override
        public void configure(final ClientDetailsServiceConfigurer clients) throws
                Exception {
            clients.withClientDetails(this.clientDetailsService);
        }
    
    }///:~
    ```

3.  How to test

    a. Url for request a token:
       ``` http://localhost:8081/oauth/token ```
       Server configuration: 
       ``` 
       server:
         port: 8081 
       ```
       
    b. Parameters:
       - grant_type=password
       - client_id=cloud
       - username=yul
       - password=123456
       
    c. Authorization Header:
       - Username=client // The client id
       - Password=123456 // The client secret
       
    d. Full Url: 
       ``` http://localhost:8081/oauth/token?grant_type=password&client_id=cloud&username=yul&password=123456 ```
    
    e. The request http method should be: ```POST``` other than ```GET```


## How to config to support refresh token?

1.  Add refresh_token grant type to the client

2.  Set up refreshTokenValiditySeconds to the client

    ``` 
    GrantType refreshTokenGrantType = GrantType.builder()
            .type("refresh_token")
            .build();
    
    ... ...
    
    Client client;
    client = Client.builder().clientId("cloud")
            .clientSecret(this.passwordEncoder.encode(CLIENT_SECRET))
            .scope(readScope)
            .scope(writeScope)
            .authorizedGrantType(pwGrantType)
            .authorizedGrantType(refreshTokenGrantType)
            .accessTokenValiditySeconds(3600)
            .refreshTokenValiditySeconds(3600 * 24)
            .autoApprove(true)
            .build();
    ```
    
3.  Define a new DefaultTokenServices Bean
    - Set the token store bean to this new DefaultTokenServices Bean
    - Set supportRefreshToken property of the new DefaultTokenServices Bean
      to be ```true```
    - Make this new DefaultTokenServices Bean to be ``` @Primary ``` because
      Spring OAuth2 already has a default one

    ``` 
    @Bean
    @Primary
    public DefaultTokenServices tokenService() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(this.tokenStore());
        tokenServices.setSupportRefreshToken(true);
        return tokenServices;
    }
    ```
    
4.  Wiring in UserDetailsService 
    - Do this because when user go to refresh a token, it is not required to ask
      the user to submit it's username and password again, just need to submit
      the refresh token only
    
    ``` 
    private final UserDetailsService userDetailsService;
    
    @Autowired
    public SslAuthorizationServerConfiguration(
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            ClientDetailsService clientDetailsService,
            UserDetailsService userDetailsService) {

        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.clientDetailsService = clientDetailsService;
        this.userDetailsService = userDetailsService;
    }
    
    ... ...
    
    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {

        endpoints.tokenStore(this.tokenStore())
                .authenticationManager(this.authenticationManager)
                .userDetailsService(this.userDetailsService)
                .accessTokenConverter(this.accessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }
    ```
    
5.  Get refresh token in practice

    a. Get a new token with extra refresh token
    
    ``` 
    http://localhost:8081/oauth/token?grant_type=password&client_id=cloud&username=yul&password=123456
    with credential: client id and client secret
    ```

    b. Refresh the token
    
    ``` 
    http://localhost:8081/oauth/token?grant_type=refresh_token&refresh_token={refreshToken}
    with credential: client id and client secret
    ```

## How to config to support accessing the details of a token 

1.  Override the method 
    ``` configure(final ClientDetailsServiceConfigurer clients) ```
    of AuthorizationServerConfigurerAdapter
    
    ```  
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws
            Exception {

        security.checkTokenAccess("permitAll()");
        super.configure(security);
    }
    ```

2.  Check the details of a token in Practice

    ``` 
    http://localhost:8081/oauth/check_token?token={token}
    with credential: client id and client secret
    ```



Resources
- [The Secure Password & Keygen Generator](https://randomkeygen.com/)
- [Customized Implementation of ClientDetailsService](https://blog.couchbase.com/oauth-2-dynamic-client-registration/)