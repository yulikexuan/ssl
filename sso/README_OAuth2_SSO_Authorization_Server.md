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


## OAuth2 Authorization Code Flow Server Set up

0.  Dependencies 

    ``` 
    <dependency>
        <groupId>org.springframework.security.oauth</groupId>
        <artifactId>spring-security-oauth2</artifactId>
        <version>2.3.6.RELEASE</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.security.oauth.boot</groupId>
        <artifactId>spring-security-oauth2-autoconfigure</artifactId>
        <version>2.1.7.RELEASE</version>
    </dependency>
    ```

1.  Define the client which is using Authorization Code Flow

    ``` 
    // DefaultLoader.java
    Client dms;
    dms = Client.builder().clientId("dms")
            .clientSecret(this.passwordEncoder.encode(CLIENT_SECRET))
            .scope(ClientScope.builder()
                    .scope("PRIVILEGE_READ")
                    .build())
            .scope(ClientScope.builder()
                    .scope("PRIVILEGE_WRITE")
                    .build())
            .authorizedGrantType(GrantType.builder()
                    .type("authorization_code")
                    .build())
            .authorizedGrantType(GrantType.builder()
                    .type("refresh_token")
                    .build())
            .redirectUris("http://localhost:8082/dms/login")
            .accessTokenValiditySeconds(3600)
            .refreshTokenValiditySeconds(3600 * 24)
            .autoApprove(true)
            .build();

    this.sslClientDetailsService.save(dms);
    
    // SslAuthorizationServerConfiguration.java
    
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws
            Exception {

        clients.withClientDetails(this.clientDetailsService);
    }

    /*
     * The URL paths provided by the framework are
     *   - /oauth/authorize (the authorization endpoint)
     *   - /oauth/token (the token endpoint)
     *   - /oauth/confirm_access (user posts approval for grants here
     *   - /oauth/error (used to render errors in the authorization server)
     *   - /oauth/check_token (used by Resource Servers to decode access tokens)
     *   - /oauth/token_key (exposes public key for token verification if using
     *     JWT tokens)
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws
            Exception {

        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();

        super.configure(security);
    }
    ```

2.  Config Resource Server

    a.  Enable #Oauth2 security expressions
        By adding dependency "spring-security-oauth2-autoconfigure" to pom,
        #Oauth2 security expressions will be added by default
    
    b.  Config resource server by extending ResourceServerConfigurerAdapter
    
        ```
        http.requestMatchers()
                // Restrict config only to "/api/users/**"
                .antMatchers("/api/users/**")
                .and()
                // Allows restricting access based upon the HttpServletRequest using
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/api/users/**")
                // Allows specifying that URLs are secured by an arbitrary expression
                .access("#oauth2.hasScope('PRIVILEGE_READ')")
                .antMatchers(HttpMethod.POST,"/api/users/**")
                .access("#oauth2.hasScope('PRIVILEGE_WRITE')")
                .antMatchers(HttpMethod.DELETE,"/api/users/**")
                .access("#oauth2.hasScope('PRIVILEGE_WRITE')");
        }
        ```

3.  Implement REST APIs

    ``` 
    @RestController
    @RequestMapping(path = "/api/users")
    public class UserController {
    
        private final IUserService userService;
        private final IUserMapper userMapper;
        private final IUserListMapper userListMapper;
        private final PasswordEncoder passwordEncoder;
    
        @Autowired
        public UserController(IUserService userService,
                              PasswordEncoder passwordEncoder) {
    
            this.userService = userService;
            this.passwordEncoder = passwordEncoder;
            this.userMapper = IUserMapper.INSTANCE;
            this.userListMapper = IUserListMapper.INSTANCE;
        }
    
        @GetMapping("/me")
        public Principal user(Principal principal) {
            return principal;
        }
    
        @GetMapping
        public List<UserDto> list() {
    
            List<User> userList =  this.userService.findAllUsers();
            UserListDto users = this.userListMapper.userListToUserListDto(userList);
    
            return users.getUsers();
        }
    
        @GetMapping("{id}")
        public UserDto view(@PathVariable("id") Long id) {
            User user = this.userService.getUserById(id).get();
            UserDto dto = this.userMapper.userToUserDto(user);
            return dto;
        }
    
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public UserDto create(@Valid UserDto userDto) {
    
            userDto.setPassword(this.passwordEncoder.encode(
                    SslSecurityConfigerAdapter.DEFAULT_SIMPLE_PW));
    
            User savedUser = this.userService.saveUser(
                    userMapper.userDtoToUser(userDto));
            userDto.setId(savedUser.getId());
    
            return userDto;
        }
    
        @PostMapping("/delete/{id}")
        @ResponseStatus(HttpStatus.OK)
        public void delete(@PathVariable("id") Long id) {
            this.userService.deleteUser(id);
        }
    
    }///:~
    ```

4.  Set up the Web Security

    a.  Make sure, the UserDetailsService is correctly configured 
    
    ``` 
    // com.yulikexuan.ssl.app.config.security.SslSecurityConfigerAdapter.java
    
    private final UserDetailsService userDetailsService;
    
    @Autowired
    public SslSecurityConfigerAdapter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authManagerBuilder)
            throws Exception {
        authManagerBuilder.parentAuthenticationManager(
                this.authenticationManager());
        authManagerBuilder.userDetailsService(this.userDetailsService);
    }
    
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {

        final DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());

        return daoAuthenticationProvider;
    }
    
    ```
    
    b.  Set up form login
    
    ``` 
    // com.yulikexuan.ssl.app.config.security.SslSecurityConfigerAdapter.java
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .accessDecisionManager(this.accessDecisionManager())
                .antMatchers(this.permitUrls)
                .permitAll()

                .anyRequest()
                .authenticated()

                .and()
                .formLogin()
                .permitAll()

                .and() // Disable X-Frame-Options in Spring Security
                .headers() // So we can user the console of h2 database
                .frameOptions()
                .disable()

                .and()
                .csrf()
                .disable();

    }//  End of configure(HttpSecurity)
    ```

    c. Remove security.basic.enabled=false from application.yml
    
5.  Make /login** and /oauth/authorize** be permitted

    ``` 
    # application.yml
    # Allow Thymeleaf templates to be reloaded at dev time
    server:
      port: 8081
    spring:
      thymeleaf:
        cache: false
      jpa:
        show-sql: false
    #    properties:
    #      hibernate:
    #        format_sql: true
      h2:
        console:
          enabled: true
          path: "/h2-console"
    app:
      security:
        permit:
          urls: /h2-console/**,/login**,/oauth/authorize**
        oauth2:
          jwt:
            signingkey: 6264BB136A72A461C3ACCFB2FC1BF
    ```
    
## Custom Claims in the Token

1.  Config Authorization Server

    a. Define a custom ``` TokenEnhancer ```
       Add an extra field “organization” to Access Token – with this SslTokenEnhancer
    
    ``` 
    public class SslTokenEnhancer implements TokenEnhancer {
    
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
                                         OAuth2Authentication authentication) {
    
            Map<String, Object> additionalInfo = Map.of("organization",
                    authentication.getName() + randomAlphabetic(4));
    
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(
                    additionalInfo);
    
            return accessToken;
        }
    
    }///:~
    ```
    
    b. Wire SslTokenEnhancer into Authoriztion Server's ``` SslAuthorizationServerConfiguration ```
    
    ``` 
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new SslTokenEnhancer();
    }
    
    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {

        JwtAccessTokenConverter tokenConverter = this.accessTokenConverter();
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers =
                List.of(this.tokenEnhancer(), tokenConverter);
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        endpoints.tokenStore(this.tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(this.authenticationManager)
                .userDetailsService(this.userDetailsService)
                .accessTokenConverter(this.accessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }
    ```

2.  Access extra claims on sso client

    ``` 
    public class AuthenticationController {
    
        private UserService userService;
        private OAuth2RestTemplate restTemplate;
    
        @Autowired
        public AuthenticationController(UserService userService,
                                        OAuth2RestTemplate restTemplate) {
    
            this.userService = userService;
            this.restTemplate = restTemplate;
        }
    
        @GetMapping("/authentication")
        public ModelAndView getAuthentication(final Principal authentication) {
    
            log.info(">>>>>>> Organization: {}",
                    this.restTemplate.getAccessToken().getAdditionalInformation()
                            .get("organization"));
    
            SslOAuth2AuthenticationDto principal =
                    this.userService.getAuthentication();
    
            return new ModelAndView("currentUserPage",
                    "principal", principal);
        }
    
    }///:~
    ```


3.  [Access Extra Claims on Resource Server](https://www.baeldung.com/spring-security-oauth-jwt)


## Asymmetric KeyPair

1.  Generate JKS Java KeyStore File
    ``` 
    keytool -genkeypair -alias sslsso -keyalg RSA -keypass sslsso -keystore sslsso.jks -storepass sslsso
    ```
    Make sure keypass and storepass are the same
    
2.  Export Public Key
    ``` 
    keytool -list -rfc --keystore sslsso.jks | openssl x509 -inform pem -pubkey 
    ```
    or 
    ``` 
    keytool -list -rfc --keystore sslsso.jks | openssl x509 -inform pem -pubkey -noout
    ```
    
    We take only our Public key and copy it to our resource server 
    src/main/resources/public.txt

3.  Maven Config

    a. Do not make the JKS file to be picked up by the maven filtering process
    b. Make sure that the JKS file is added to application classpath via Spring
       Boot Maven Plugin - addResources
       
    ``` 
    <build>

        <plugins>

            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <addResources>true</addResources>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <nonFilteredFileExtensions>
                        <nonFilteredFileExtension>jks</nonFilteredFileExtension>
                    </nonFilteredFileExtensions>
                </configuration>
            </plugin>

        </plugins>

    </build>
    ```

4.  Config Authorization Server
    
    a. Configure ```JwtAccessTokenConverter``` to use the KeyPair from sslsso.jks
    ``` 
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {

        final JwtAccessTokenConverter accessTokenConverter =
                new JwtAccessTokenConverter();

        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource(KEY_STORE_FILE_NAME),
                KEY_STORE_ALIAS.toCharArray());

        accessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(
                KEY_STORE_ALIAS));

        return accessTokenConverter;
    }
    ```

5.  Config the sso client

    a. Add public key file, ```sslkey.pub``` , to ```resource``` package of the client

    b. Remove ```security.oauth2.resource.jwt.keyUri``` and ```security.oauth2.resource.jwt.keyValue``` from application.yml
    
    c. Add ```security.oauth2.resource.jwt.plublic-key-location: classpath:sslkey.pub``` to application.yml

    ``` 
    security:
      oauth2:
        sso:
          loginPage: /login
        client:
          clientId: dms
          clientSecret: 2PGlgRk9Mv
          accessTokenUri: http://localhost:8081/ums/oauth/token
          userAuthorizationUri: http://localhost:8081/ums/oauth/authorize
          clientAuthenticationScheme: form
        resource:
          userInfoUri: http://localhost:8081/ums/api/users/me
          # preferTokenInfo: false
          jwt:
            public-key-location: classpath:sslkey.pub
    #        keyUri: http://localhost:8081/ums/oauth/token
    #        keyValue: 6264BB136A72A461C3ACCFB2FC1BF
    ```

### Debug

For Authorization Server: 
``` 
OAuth2AuthenticationProcessingFilter 
```
For Sso Client: 
``` 
OAuth2ClientAuthenticationProcessingFilter 
```

## Create OAuth2 Resource Server for SSO

Very similar to the config of the Authorization Server

#### Config Access Token Converter 

1.  Create a new ```AccessTokenConverter``` which extends 
    ```DefaultAccessTokenConverter``` in order to access extra claims on 
    Resource Server
    
    ``` 
    @Component
    public class SslAccessTokenConverter extends DefaultAccessTokenConverter {
    
        @Override
        public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
            OAuth2Authentication authentication = super.extractAuthentication(claims);
            authentication.setDetails(claims);
            return authentication;
        }
    
    }///:~
    ```

2.  Inject ```SslAccessTokenConverter``` into ```SslResourceServerConfigurerAdapter``` 
    for creating ```JwtAccessTokenConverter```
    
3.  Put public key file, which is named as "sslkey.pub", into ```resources``` 
    folder
    
4.  Setup public key as the verifier key into ``` JwtAccessTokenConverter ```
    by calling ``` converter.setVerifierKey(sslkey); ```

    ``` 
    @Configuration
    @EnableResourceServer
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(securedEnabled = true)
    public class SslResourceServerConfigurerAdapter
            extends ResourceServerConfigurerAdapter {

        @Value("classpath:sslkey.pub")
        Resource sslPublicKeyResource;
    
        private final DefaultAccessTokenConverter sslAccessTokenConverter;
    
        @Autowired
        public SslResourceServerConfigurerAdapter(
                DefaultAccessTokenConverter sslAccessTokenConverter) {
    
            this.sslAccessTokenConverter = sslAccessTokenConverter;
        }
    
        // Beans:
    
        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
    
            final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    
            // In case there is additional custom claims in JWT token
            converter.setAccessTokenConverter(this.sslAccessTokenConverter);
    
            // Set up public key as verifier key into the jwt access token converter
            String sslkey = null;
            try {
                sslkey = IOUtils.toString(sslPublicKeyResource.getInputStream(),
                        Charset.forName("UTF-8"));
            } catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
            converter.setVerifierKey(sslkey);
    
            return converter;
        }
        ... ... ...
    }
    ```

#### Create JWT Token Store which is a Persistence Interface for OAuth2 Tokens

0.  JwtTokenStore is also the persistence strategy for token storage for JWT

1.  JwtTokenStore is a TokenStore implementation that just reads data from the 
    tokens themselves; Not really a store since it never persists anything, and 
    methods like ```getAccessToken(OAuth2Authentication)``` always return null
     
2.  JwtTokenStore is a useful tool since it translates access tokens to and from 
    authentications
     
3.  Use this wherever a TokenStore is needed, but remember to use the same 
    ```JwtAccessTokenConverter``` instance (or one with the same verifier) as 
    was used when the tokens were minted.
    
    ``` 
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(this.accessTokenConverter());
    }
    ```

#### Create the Token Service

1.  Create a instance of ```DefaultTokenServices```

2.  Assign the JwtTokenStore bean to it

3.  Config the token services instance to support "Refresh Token"

4.  Make this created token services bean be ```Primary```

    ``` 
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(this.tokenStore());
        tokenServices.setSupportRefreshToken(true);
        return tokenServices;
    }
    ```

#### Config ```ResourceServerSecurityConfigurer```

1.  Assign the created token services bean to 

    ``` 
    @Override
    public void configure(ResourceServerSecurityConfigurer resourcesConfigure)
            throws Exception {

        resourcesConfigure.tokenServices(this.tokenServices());
    }
    ```

#### Config HttpSecurity of Resource Service

    ``` 
    @Override
    public void configure(final HttpSecurity http) throws Exception {

        http.requestMatchers()
                .antMatchers("/api/**")
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/welcome")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/rates")
                .access("#oauth2.hasScope('PRIVILEGE_READ')")
                .anyRequest()
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .csrf()
                .disable();
    }
    ```

#### Sprcify the location of public key of JWT in application.yml

    ```
    server:
      port: 8089
      servlet:
        context-path: /currency
    security:
      oauth2:
        resource:
          jwt:
             public-key-location: classpath:sslkey.pub
    ```

## OAuth2 Authorization Code Flow in Practice

1.  Get Authorization Code

    The url to get authorization code from a browser
    ``` http://localhost:8081/ums/oauth/authorize?client_id=dms&response_type=code ```
    
    Then if having redirect setted up, we will be redirected to
    ``` http://localhost:8082/dms/login?code=WC6pd2 ``` 

    Here 'WC6pd2' is authorization code for the third party application

2.  Get Access Token with the Authorization Code

    ``` http://localhost:8081/ums/oauth/token ```
    
    [Header]
    Content-Type=application/x-www-form-urlencoded
    
    [Form Body]
    grant_type=authorization_code
    code=WC6pd2
    redirect_uri=http://localhost:8082/login


### Resources
- [Spring Security Reference Html5](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
- [Spring Security Reference PDF](https://docs.spring.io/spring-security/site/docs/current/reference/pdf/spring-security-reference.pdf)
- [Simple Single Sign-On with Spring Security OAuth2](https://www.baeldung.com/sso-spring-security-oauth2)
- [Using JWT with Spring Security OAuth](https://www.baeldung.com/spring-security-oauth-jwt)
- [The Secure Password & Keygen Generator](https://randomkeygen.com/)
- [Customized Implementation of ClientDetailsService](https://blog.couchbase.com/oauth-2-dynamic-client-registration/)
- [Enable #oauth2 security expressions](https://stackoverflow.com/questions/29797721/oauth2-security-expressions-on-method-level)