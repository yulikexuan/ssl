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

## OAuth2 - Client Credentials Flow

1.  Create new Client for OAuth2 Client Credentials Flow

    ``` 
    Client sslClient;
    sslClient = Client.builder().clientId("sslClient")
            .clientSecret(this.passwordEncoder.encode(CLIENT_SECRET))
            .scope(ClientScope.builder()
                    .scope("PRIVILEGE_READ")
                    .build())
            .scope(ClientScope.builder()
                    .scope("PRIVILEGE_WRITE")
                    .build())
            .authorizedGrantType(GrantType.builder()
                    .type("client_credentials")
                    .build())
            .accessTokenValiditySeconds(3600)
            .refreshTokenValiditySeconds(3600 * 24)
            .autoApprove(true)
            .build();
    ```
    
    - The spec states the ClientCredentials grant type MUST NOT allow for the 
      issuing of refresh tokens


## Two-Factor Authentication 

### With Google Authenticator

1.  Maven Config

    ``` 
    <dependency>
        <groupId>org.jboss.aerogear</groupId>
        <artifactId>aerogear-otp-java</artifactId>
        <version>1.0.0</version>
        <scope>compile</scope>
    </dependency>
    ```
    
2.  The Domain Model: The secret which is created by using ```aerogear-otp-java```
    should be set to new user by default

    ``` 
    @Data
    @Entity
    @NoArgsConstructor
    @Builder @AllArgsConstructor
    public class User {

        ... ...    
        @Column
        private String secret;
        ... ...
    }

    public static final String DEFAULT_USER_SECRET = Base32.random();
    
    this.userService.saveUser(User.builder()
            .username("yul")
            .email("yu.li@tecsys.com")
            .password(pw)
            .enabled(true)
            .roles(Set.of(roleUser))
            .created(Timestamp.from(Instant.now()))
            .secret(DEFAULT_USER_SECRET)
            .build());
    ```
    
3.  Extra Login Parameter

    a. Adjust the security configuration to accept extra parameter, 
       the verification code
       
    ``` 
    public class SslWebAuthenticationDetails extends WebAuthenticationDetails {
    
        private String verificationCode;
    
        public SslWebAuthenticationDetails(HttpServletRequest request) {
            super(request);
            this.verificationCode = request.getParameter("verificationCode");
        }
    
        public String getVerificationCode() {
            return verificationCode;
        }
    
        public Optional<String> getVerificationCodeOpt() {
            return Optional.ofNullable(this.verificationCode);
        }
    
        public void setVerificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
        }
    
    } 
    
    @Component
    public class SslWebAuthenticationDetailsSource implements
            AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {
    
        @Override
        public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
            return new SslWebAuthenticationDetails(context);
        }
    
    } 
    ```
    
    b. Wire in SslWebAuthenticationDetailsSource
    ``` 
    private final SslWebAuthenticationDetailsSource authenticationDetailsSource;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                ... ...
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/dosignin")
                .defaultSuccessUrl("/vcode/request")
                .authenticationDetailsSource(this.authenticationDetailsSource)
                ... ...

    }//  End of configure(HttpSecurity)
    ``` 
    
    c. Add the extra parameter to our login form in loginPage.html
    ``` 
    <div class="form-group">
        <label class="control-label col-xs-2" for="code">
            Verification Code:
        </label>
        <div class="col-xs-10">
            <input id="code" type="text" name="verificationCode" />
        </div>
    </div>
    ```
    
    d. Customize ```DaoAuthenticationProvider```
    
    ``` 
    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        super.additionalAuthenticationChecks(userDetails, authentication);

        String verificationCode =
                ((SslWebAuthenticationDetails) authentication.getDetails())
                        .getVerificationCode();

        System.out.printf("Verification code from request parameter: %1$s",
                verificationCode);

        String username = authentication.getName();

        if (!"admin".equals(username)) {
            final Optional<User> userOpt = this.userService.findUserByUsername(
                    username);

            String secret = userOpt.map(User::getSecret)
                    .orElse("");

            final Totp totp = new Totp(secret);

            try {
                if (!totp.verify(verificationCode)) {
                    throw new BadCredentialsException("Invalid verification code!");
                }
            } catch (final Exception e) {
                throw new BadCredentialsException("Invalid verification code!");
            }
        }

    }
    ```
    
    e. Wire in the customized dao auth provider
    
    ``` 
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {

        final DaoAuthenticationProvider daoAuthenticationProvider =
                new SslDaoAuthenticationProvider(this.userService);

        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());

        return daoAuthenticationProvider;
    }
    ```
    
4.  Before login, the user should be able to get the verification code

    a. Creatge a page for specifying the username in order to generate a new 
       verification code
       ``` vcodeRequestPage.html ```
    
    b. Create a new controller method in order to fetch the user from db and 
       bring the user to ``` qrcode.html``` in order to generate the bar code
       
    ``` 
    @Controller
    @RequestMapping(path = "/vcode")
    public class VerficationCodeController {
    
        public static String QR_PREFIX =
                "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    
        public static String APP_NAME = "ums";
    
        private final IUserService userService;
    
        @Autowired
        public VerficationCodeController(IUserService userService) {
            this.userService = userService;
        }
    
        @GetMapping(path = "/request")
        public ModelAndView requestPage() {
            return new ModelAndView("vcodeRequestPage");
        }
    
        @PostMapping(path = "/gen")
        public ModelAndView requestVerificationCode(
                @ModelAttribute("user") User user, BindingResult result,
                ModelMap model) {
    
            Optional<User> userOpt = this.userService.findUserByUsername(
                    user.getUsername());
    
            return new ModelAndView("qrcode", "user",
                    userOpt.orElse(null));
        }
    
        @GetMapping
        @ResponseBody
        public Map<String, String> getQRUrl(
                @RequestParam("username") final String username)
                throws UnsupportedEncodingException {
    
            final Map<String, String> result = new HashMap<String, String>();
            User user = this.userService.findUserByUsername(username).orElse(null);
    
            if (user == null) {
                result.put("url", "");
            } else {
                result.put("url", generateQRUrl(user.getSecret(),
                        user.getUsername()));
            }
    
            return result;
        }
    
        private String generateQRUrl(String secret, String username)
                throws UnsupportedEncodingException {
    
            return QR_PREFIX + URLEncoder.encode(
                    String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                            APP_NAME, username, secret, APP_NAME), "UTF-8");
        }
    }
    ```
    The html page for scan the bar code: 
    ```
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta charset="UTF-8">
        <title>Verification Code Request</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    </head>
    <body>
        <div class="container">
    
            <h3 style="color:#FF8800;margin:1em auto;">Verification Code Request</h3>
    
            <form th:action="@{/vcode/gen}" method="post" class="form-horizontal" modelAttribute="user">
                <div class="form-group">
                    <label class="control-label col-xs-2" for="username">Username: </label>
                    <div class="col-xs-10">
                        <input id="username" type="text" name="username" />
                    </div>
                </div>
                <input type="submit" class="btn btn-primary" value="Go!" />
            </form>
    
        </div>
    </body>
    </html>
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

## Integration Test for OAuth2 with REST-assured

    ``` 
    @Slf4j
    @ActiveProfiles("test") // Using application-test.yml in resources
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @DisplayName("OAuth2 Authorization Server Test - ")
    class SslAuthorizationServerConfigurationIT {
        ... ...
        @DisplayName("Test jwt token - ")
        @RepeatedTest(value = 2, name = "{displayName} : {currentRepetition} / {totalRepetitions}")
        @Test
        void able_To_Get_JWT_Token() {
    
            // Given
    
            // When
            given().auth()
                    /*
                     * preemptive(): Returns the preemptive authentication view.
                     * This means that the authentication details are sent in the
                     * request header regardless if the server has challenged for
                     * authentication or not
                     */
                    .preemptive()
                    .basic("sslClient", DefaultLoader.CLIENT_SECRET)
                    .with()
                    .formParam(this.grantTypeParamName, this.grantTypeParamValue)
                    .when()
                    .post(this.tokenRequestUrl)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(HttpStatus.SC_OK)
                    .body(this.jwtTokenNodeName, notNullValue())
                    .and()
                    .time(lessThan(1500L));
        }
    }
    ```


### Debug

For simple username & password authentication:
``` 
UsernamePasswordAuthenticationFilter
```

For OAuth2 Authorization Server: 
``` 
OAuth2AuthenticationProcessingFilter 
```

For OAuth2 Sso Client: 
``` 
OAuth2ClientAuthenticationProcessingFilter 
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

3.  Get Access Token for Client with "client_credentials" Grant Type

    ``` http://localhost:8081/ums/oauth/token ```
    
    Authorization: Basic Auth
    Username: sslClient
    Password: 2PGlgRk9Mv
    
    Form Params:
    "grant_type": "client_credentials"
    

### Resources
- [Spring Security Reference Html5](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
- [Spring Security Reference PDF](https://docs.spring.io/spring-security/site/docs/current/reference/pdf/spring-security-reference.pdf)
- [Simple Single Sign-On with Spring Security OAuth2](https://www.baeldung.com/sso-spring-security-oauth2)
- [Using JWT with Spring Security OAuth](https://www.baeldung.com/spring-security-oauth-jwt)
- [The Secure Password & Keygen Generator](https://randomkeygen.com/)
- [Customized Implementation of ClientDetailsService](https://blog.couchbase.com/oauth-2-dynamic-client-registration/)
- [Enable #oauth2 security expressions](https://stackoverflow.com/questions/29797721/oauth2-security-expressions-on-method-level)
- [REST-assured](http://rest-assured.io/)
- [A Guide to REST-assured](https://www.baeldung.com/rest-assured-tutorial)
- [REST-assured User Guide](https://github.com/rest-assured/rest-assured/wiki/Usage)
- [REST Assured Authentication](https://www.baeldung.com/rest-assured-authentication)
- [Two Factor Auth with Spring Security](https://www.baeldung.com/spring-security-two-factor-authentication-with-soft-token)
