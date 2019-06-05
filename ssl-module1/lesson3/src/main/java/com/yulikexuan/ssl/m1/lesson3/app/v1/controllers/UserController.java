//: com.yulikexuan.ssl.m1.lesson3.app.v1.controllers.UserController.java


package com.yulikexuan.ssl.m1.lesson3.app.v1.controllers;


import com.yulikexuan.ssl.m1.lesson3.app.v1.mapper.IUserListMapper;
import com.yulikexuan.ssl.m1.lesson3.app.v1.mapper.IUserMapper;
import com.yulikexuan.ssl.m1.lesson3.app.v1.model.UserDto;
import com.yulikexuan.ssl.m1.lesson3.app.v1.model.UserListDto;
import com.yulikexuan.ssl.m1.lesson3.domain.model.User;
import com.yulikexuan.ssl.m1.lesson3.domain.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/")
public class UserController {

    private final IUserService userService;
    private final IUserMapper userMapper;
    private final IUserListMapper userListMapper;

    @Autowired
    public UserController(IUserService userService) {

        this.userService = userService;
        this.userMapper = IUserMapper.INSTANCE;
        this.userListMapper = IUserListMapper.INSTANCE;
    }

    @RequestMapping
    public ModelAndView list() {

        List<User> userList =  this.userService.findAllUsers();

        UserListDto users = this.userListMapper.userListToUserListDto(userList);

        return new ModelAndView("users/list", "users",
                users.getUsers());
    }

    @RequestMapping("{id}")
    public ModelAndView view(@PathVariable("id") Long id) {
        User user = this.userService.getUserById(id).get();
        UserDto dto = this.userMapper.userToUserDto(user);
        return new ModelAndView("users/view", "user", dto);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@ModelAttribute(value="user") UserDto user) {
        user.setId(-1L);
        return "users/form";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@Valid UserDto userDto, BindingResult result,
                               RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return new ModelAndView("users/form",
                    "formErrors", result.getAllErrors());
        }
        User savedUser = this.userService.saveUser(
                userMapper.userDtoToUser(userDto));
        userDto.setId(savedUser.getId());
        redirect.addFlashAttribute("globalMessage",
                "Successfully created a new userDto");
        return new ModelAndView("redirect:/{userDto.id}",
                "userDto.id", userDto.getId());
    }

    @RequestMapping("foo")
    public String foo() {
        throw new RuntimeException("Expected exception in controller");
    }
    @RequestMapping(value = "delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "modify/{id}", method = RequestMethod.GET)
    public ModelAndView modifyForm(@PathVariable("id") Long id) {
        User user = this.userService.getUserById(id).get();
        UserDto dto = this.userMapper.userToUserDto(user);
        return new ModelAndView("users/form", "user", dto);
    }

}///:~