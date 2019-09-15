//: com.yulikexuan.ssl.app.controllers.UserController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.app.config.security.SslSecurityConfigerAdapter;
import com.yulikexuan.ssl.app.model.IUserListMapper;
import com.yulikexuan.ssl.app.model.IUserMapper;
import com.yulikexuan.ssl.app.model.UserDto;
import com.yulikexuan.ssl.app.model.UserListDto;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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