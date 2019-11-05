//: com.yulikexuan.ssl.app.config.mvc.LssWebMvcConfigurer.java


package com.yulikexuan.ssl.app.config.mvc;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Slf4j
@EnableWebMvc
@Configuration
public class LssWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("loginPage");
    }

}///:~