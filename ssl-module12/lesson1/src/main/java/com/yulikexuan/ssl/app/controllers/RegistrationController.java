//: com.yulikexuan.ssl.app.controllers.RegistrationController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.app.mapper.IUserMapper;
import com.yulikexuan.ssl.app.model.UserDto;
import com.yulikexuan.ssl.domain.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@Controller
public class RegistrationController {

    private final IUserService userService;
    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(IUserService userService,
                                  PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = IUserMapper.INSTANCE;
    }

    @RequestMapping("signup")
    public ModelAndView getRegistrationForm() {
        return new ModelAndView("registrationPage", "user",
                new UserDto());
    }

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

}///:~