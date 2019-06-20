//: com.yulikexuan.ssl.app.controllers.RegistrationController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.app.events.OnRegistrationCompleteEvent;
import com.yulikexuan.ssl.app.mapper.IUserMapper;
import com.yulikexuan.ssl.app.model.UserDto;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.model.VerificationToken;
import com.yulikexuan.ssl.domain.services.EmailExistsException;
import com.yulikexuan.ssl.domain.services.IUserService;
import com.yulikexuan.ssl.domain.services.IVerificationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;


@Slf4j
@Controller
public class RegistrationController {

    private final IUserService userService;
    private final IVerificationTokenService verificationTokenService;
    private final ApplicationEventPublisher eventPublisher;

    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(
            IUserService userService,
            IVerificationTokenService verificationTokenService,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher) {

        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = IUserMapper.INSTANCE;
    }

    @RequestMapping("/signup")
    public ModelAndView getRegistrationForm() {
        return new ModelAndView("registrationPage", "user",
                new UserDto());
    }

    @RequestMapping("/user/register")
    public ModelAndView registerUser(@Valid final UserDto user,
                                     final BindingResult result,
                                     final HttpServletRequest request) {

        if (result.hasErrors()) {
            return new ModelAndView("registrationPage",
                    "userDto", user);
        }

        try {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            user.setEnabled(false);

            User saveUser = this.userService.saveUser(
                    userMapper.userDtoToUser(user));

            final String token = UUID.randomUUID().toString();
            final VerificationToken vToken = VerificationToken.builder()
                    .token(token)
                    .user(saveUser)
                    .build();

            this.verificationTokenService.save(vToken);
            final String appUri = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host(request.getServerName())
                    .port(request.getServerPort())
                    .path(request.getContextPath())
                    .path("/registrationConfirm")
                    .queryParam("token", token)
                    .build()
                    .toUriString();

            log.info(">>>>>>> Application URI: '{}'", appUri);

            eventPublisher.publishEvent(
                    new OnRegistrationCompleteEvent(vToken, appUri));

        } catch (EmailExistsException e) {
            result.addError(new FieldError("user", "email", e.getMessage()));
            return new ModelAndView("registrationPage", "user", user);
        }

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(
            final Model model, @RequestParam("token") final String token,
            final RedirectAttributes redirectAttributes) {

        VerificationToken verificationToken =
                verificationTokenService.findByToken(token).orElse(null);

        if (verificationToken == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Invalid account confirmation token.");
            return new ModelAndView("redirect:/login");
        }

        final User user = verificationToken.getUser();

        log.info(">>>>>>> Expire Date: '{}'", verificationToken.getExpiryDate());

//        final Calendar cal = Calendar.getInstance();
//        if ((verificationToken.getExpiryDate().getTime() -
//                cal.getTime().getTime()) <= 0) {
//
//            redirectAttributes.addFlashAttribute("errorMessage", "Your registration token has expired. Please register again.");
//            return new ModelAndView("redirect:/login");
//        }

        user.setEnabled(true);
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("message",
                "Your account verified successfully");

        return new ModelAndView("redirect:/login");
    }

}///:~