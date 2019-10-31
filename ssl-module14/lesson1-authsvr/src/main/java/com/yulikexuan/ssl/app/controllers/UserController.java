//: com.yulikexuan.ssl.app.controllers.UserController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.app.config.security.SslSecurityConfigerAdapter;
import com.yulikexuan.ssl.app.model.*;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private final IUserService userService;
    private final IUserMapper userMapper;
    private final IUserListMapper userListMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;

    @Autowired
    public UserController(IUserService userService,
                          PasswordEncoder passwordEncoder,
                          TokenStore tokenStore) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenStore = tokenStore;
        this.userMapper = IUserMapper.INSTANCE;
        this.userListMapper = IUserListMapper.INSTANCE;
    }

    @GetMapping("/current")
    public SslOAuth2AuthenticationDto current(final Principal principal) {

        User user = this.userService.findUserByUsername(principal.getName())
                .orElseGet(() -> {
                    User unknownUser = new User();
                    unknownUser.setUsername(principal.getName());
                    return unknownUser;
                });

        UserDto userDto = this.userMapper.userToUserDto(user);

        SslOAuth2AuthenticationDto authenticationDto = null;
        if (OAuth2Authentication.class.isInstance(principal)) {
            final OAuth2Authentication authentication =
                    (OAuth2Authentication) principal;
            OAuth2AuthenticationDetails authDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();

            String tokenValue = authDetails.getTokenValue();
            OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(
                    tokenValue);

            String organization = (String)accessToken.getAdditionalInformation()
                    .get("organization");

            authenticationDto = SslOAuth2AuthenticationDto.builder()
                    .name(principal.getName())
                    .userDto(userDto)
                    .authenticated(authentication.isAuthenticated())
                    .sessionId(authDetails.getSessionId())
                    .tokenType(authDetails.getTokenType())
                    .tokenValue(tokenValue)
                    .organization(organization)
                    .build();
        } else {
            authenticationDto = SslOAuth2AuthenticationDto.builder()
                    .userDto(userDto)
                    .build();
        }

        return authenticationDto;
    }

    /*
     * Reserved by Spring Boot OAuth2 Sso Authentication
     *
     * See lesson2-client-dms/src/main/resources/application.yml
     * security.oauth2.resource.userInfoUri: http://localhost:8081/ums/api/users/me
     */
    @GetMapping("/me")
    public Principal user(final Principal principal) {
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