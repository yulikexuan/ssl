# Setup OAuth2 Resource Server with Spring Security

0.  Very similar to the config of the Authorization Server

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