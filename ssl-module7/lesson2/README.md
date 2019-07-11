# Project Title

## Authentication with Users in Database   

### How to set up

1.  Add a method to IUserRepository

    ```
    List<User> findByUsername(String username);
    ```
    
2.  Add a method to IUserService interface

    ```
    Optional<User> findUserByUsername(String username);
    ```

3.  Implement the new method in UserService

    ```
    @Override
    public Optional<User> findUserByUsername(String username) {
        return this.userRepository.findByUsername(username).stream()
                .findAny();
    }
    ```

4.  Define UserDetailsService instance and add a reference field of 
    IUserService to it;
    
    ```
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return this.userService.findUserByUsername(username)
                .map(LssUserDetailsService::userToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with username: " + username));
    }
    
    
    UserDetails userToUserDetails(
            com.yulikexuan.ssl.domain.model.User lssUser) {

        return User.builder()
                .username(lssUser.getUsername())
                .password(lssUser.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .authorities(new SimpleGrantedAuthority(ROLE_USER))
                .build();
    }
    ```

5.  Reconfig AuthenticationManagerBuilder in LssSecurityConfig class

    ```
    private final UserDetailsService userDetailsService;

    @Autowired
    public LssSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authManagerBuilder)
            throws Exception {

        // authManagerBuilder.inMemoryAuthentication()
        //         .withUser("yul")
        //         .password("{noop}123456")
        //         .roles("USER");

        authManagerBuilder.userDetailsService(this.userDetailsService);

    } 
    ```
    
6.  Specify an instance of PasswordEncoder

    a. Define a Bean of PasswordEncoder into LssSecurityConfig
    
       ``` 
       @Bean
       public PasswordEncoder passwordEncoder() {
           return PasswordEncoderFactories.createDelegatingPasswordEncoder();
       }
       ```
       
    b. Add a new Bean of PasswordEncoder into RegistrationController
    
       ``` 
       private final PasswordEncoder passwordEncoder;
       
       @RequestMapping("user/register")
       public ModelAndView registerUser(@Valid final UserDto user,
                                        final BindingResult result) {
    
           if (result.hasErrors()) {
               return new ModelAndView("registrationPage",
                       "user", user);
           }
    
           user.setPassword(this.passwordEncoder.encode(user.getPassword()));
    
           this.userService.saveUser(
                   userMapper.userDtoToUser(user));
    
           return new ModelAndView("redirect:/login");
       }
       ```
       
    c. Add a new Bean of PasswordEncoder into UserController
    
       ``` 
       private final PasswordEncoder passwordEncoder;
       
       @RequestMapping(method = RequestMethod.POST)
       public ModelAndView create(@Valid UserDto userDto, BindingResult result,
                                   RedirectAttributes redirect) {
                                   
           if (result.hasErrors()) {
                return new ModelAndView("users/form",
                        "formErrors", result.getAllErrors());
           }
           
           userDto.setPassword(this.passwordEncoder.encode(
                   LssSecurityConfig.DEFAULT_SIMPLE_PW));
                   
           User savedUser = this.userService.saveUser(
                   userMapper.userDtoToUser(userDto));
                   
           userDto.setId(savedUser.getId());
           
           redirect.addFlashAttribute("globalMessage",
                   "Successfully created a new userDto");
                   
           return new ModelAndView("redirect:/{userDto.id}",
                   "userDto.id", userDto.getId());
       }
       ```

    d. Also add PasswordEncoder Bean into DefaultLoader if needed

## Send account activatation email   

### How to set up 

0.  Add maven dependency for mail

    ``` 
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
    ```

1.  Create domain model for VerificationToken

2.  Create one to one relationship between User and VerificationToken

3.  Create repository and service for VerificationToken

4.  Add "enabled", a boolean field, to User model

5.  Apply new "enabled" property in LssUserDetailsService

6.  Add new features to RegistrationController

    - Set "enabled" to be false for new registering user in RegistrationController
    - Create new VerificationToken for new register user
        * Token String
        * Application URI
        * Save new VerificationToken 
        * Send email to user (User email and event components of Spring framework)
    - Create new handler method to confirm the registration


## Send password-reset email   

### How to set up 

0 . It's similar as how to set up account activatation 

1.  One thing different is before saving new password, we need to authenticate
    the user manually
    

## Custom Security Expression

### The Framework

1.  The Method Expression Configuration class:
    
    ``` 
    org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration    
    ```
    - Base Configuration for enabling global method security  
    - Classes may extend this class to customize the defaults 
    - Must be sure to specify the EnableGlobalMethodSecurity annotation on the subclass

2.  Method Security Expression Handler
    ``` 
    org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
    ```
    
3.  Security Expression Root
    ``` 
    org.springframework.security.access.expression.SecurityExpressionRoot
    ```
    
### How to implement custom security expression

1.  Create security expression operation class ``` SslSecurityExpressionOperation ```
    - Extend ```SecurityExpressionRoot``` 
        * Add new expression method
          ``` 
          public boolean isAdmin() {
              return Optional.ofNullable(this.getPrincipal())
                      .map(this::isUserHasAdminRole)
                      .orElse(false);
          }
      
          private boolean isUserHasAdminRole(Object o) {
      
              if ((o == null) || !(o instanceof User)) {
                  return false;
              }
      
              final User user = (User)o;
              return user.getAuthorities().contains(
                      new SimpleGrantedAuthority("ROLE_ADMIN"));
          }
          ```
    - Implement MethodSecurityExpressionOperations
        * Copy content of class ```MethodSecurityExpressionRoot``` into ``` SslSecurityExpressionOperation ```
        * Cannot extend ```MethodSecurityExpressionRoot``` directly because it is package private
        
2.  Create Method Security Expression Handler ``` SslMethodSecurityExpressionHandler ```
    
    - Extend ``` DefaultMethodSecurityExpressionHandler ```
    
        * Override ```createSecurityExpressionRoot``` method
        
        ``` 
        @Override
        protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
                Authentication authentication, MethodInvocation invocation) {
    
            SslSecurityExpressionOperation root =
                    new SslSecurityExpressionOperation(authentication);
    
            root.setThis(invocation.getThis());
            root.setPermissionEvaluator(getPermissionEvaluator());
            root.setTrustResolver(getTrustResolver());
            root.setRoleHierarchy(getRoleHierarchy());
            root.setDefaultRolePrefix(getDefaultRolePrefix());
    
            return root;
        }
        ```

3.  Create Method Security Configuration class ``` SslMethodSecurityConfigurer ```

    - Extend ``` GlobalMethodSecurityConfiguration ``` class
    - Override ``` createExpressionHandler ``` method
    
        ``` 
        @Configuration
        @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // prePostEnabled is REQUIRED!
        public class SslMethodSecurityConfigurer extends
                GlobalMethodSecurityConfiguration {
        
            @Override
            protected MethodSecurityExpressionHandler createExpressionHandler() {
                return new SslMethodSecurityExpressionHandler();
            }
        
        }///:~
        ```

4.  Using new expression ``` isAdmin() ``` in ``` UserController ```
    
        ``` 
        @PreAuthorize("isAdmin()")
        @GetMapping
        public ModelAndView list() {
    
            List<User> userList =  this.userService.findAllUsers();
    
            UserListDto users = this.userListMapper.userListToUserListDto(userList);
    
            return new ModelAndView("users/list", "users",
                    users.getUsers());
        }
        ```

## Introduction to Storing Passwords

### How passwords are actually compromised

1.  Passive Attacks (被动攻击)

    - These are still online attacks but have no interaction with the user, or their user account
        - Wire Sniffing
        - Typical man in the middle attacks
        - A wide range of replay attacks
        
2.  Active online attack

    - The attacker interacts with the useraccount through the standard authentication channels
    - Go through the site, or any other channel that the user typically uses to authenticate and try to guess the password

3.   **Offline Attack**:
     **The most damaging and also the most common type of attack**
     
     - The attacker gets access to the database of the system
     - Uses offline methods and offline heuristics to crack the passwords
         * Brute force attacks
         * Dictionary attacks
         * Hybrid, sealable, rule­based attacks
         * Pre­compute hashes, rainbow attacks
     - The problem with offline attacks is that they require a data breach
       They require the attacker to get a hold of your systems database
       - **Breach: 突破口 an action that breaks an agreement to behave in a particular way**

4.   Non­technical attack


### Lay the groundwork for safe password storage practices to Mitigate password Attacks

1.  The assumption here is that the first line of defense has failed, and the data of the system got compromised

2.  If the security at one level fails, that doesn't mean that the attacker should now have access to everything
    - So if one layer of security fails, then we have another layer and another layer to take its place and make sure that the data of our users does not get compromised.
    - Never going to actually store the raw password.Storing the raw plain text password is just asking for trouble
    - Hash the password: apply a one­way hash function to the password using of salts
    - Throw away the password
    
3.  Validate Password:
    - Take the password from the user
    - Apply the same one­way hash function plus the salt to that password 
    - Compare that value to the value that we have stored in our database

4.  The complex part and the important part is just a matter of 
    **Choosing the one­way hash function and the salt**

### Why Salting

### Why Stretching

### Custom Password Encoder



### Resources:  
- [Guide to Spring Email](https://www.baeldung.com/spring-email)
- [Set up h2 console with spring security](https://dzone.com/articles/using-the-h2-database-console-in-spring-boot-with)
- [Spring Events](https://www.baeldung.com/spring-events)
- [Manually Authenticate User with Spring Security](https://www.baeldung.com/manually-set-user-authentication-spring-security)
- [Immutable Map Implementations in Java with Guava](https://www.baeldung.com/java-immutable-maps)
- [Intro to Spring Security Expressions](https://www.baeldung.com/spring-security-expressions)
- [Introduction to Spring Method Security](https://www.baeldung.com/spring-security-method-security)
- [Security Expression Operations](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/access/expression/SecurityExpressionOperations.html)
- [A Custom Security Expression with Spring Security](https://www.baeldung.com/spring-security-create-new-custom-security-expression)
- [Lesson 4 of Module 5](https://courses.baeldung.com/courses/62597/lectures/924448)
- [Adding Custom Password Encoder](https://dzone.com/articles/password-encoder-migration-with-spring-security-5)
