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


Resources
- [The Secure Password & Keygen Generator](https://randomkeygen.com/)