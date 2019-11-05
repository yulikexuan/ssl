//: com.yulikexuan.ssl.app.controllers.VerficationCodeController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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

    @RequestMapping(path = "/gen")
    public ModelAndView requestVerificationCode(
            @RequestParam("username") final String username) {

        Optional<User> userOpt = this.userService.findUserByUsername(username);
        User user = userOpt.orElse(null);
        if (user != null) {
            user.setVerificationCode("383106");
            this.userService.saveUser(user);
        }

        return new ModelAndView("qrcode", "user", user);
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
            result.put("url", generateQRUrl(user.getVerificationCode(),
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

}///:~