# Project Title

Authentication with Users in Database   

## How to set up

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
    
       