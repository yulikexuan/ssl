//: com.yulikexuan.ssl.app.config.mvc.LssWebMvcConfigurer.java


package com.yulikexuan.ssl.app.config.mvc;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.*;


@Slf4j
@EnableWebMvc
@Configuration
public class LssWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index");
        registry.addViewController("/index");
        registry.addViewController("/securedPage");
    }

}///:~