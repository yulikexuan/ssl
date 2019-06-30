//: com.yulikexuan.ssl.app.config.mvc.LssWebMvcConfigurer.java


package com.yulikexuan.ssl.app.config.mvc;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Slf4j
@EnableWebMvc
@Configuration
public class LssWebMvcConfigurer implements WebMvcConfigurer {

    /*
     * Register view controllers that create a direct mapping between the URL
     * and the view name using the ViewControllerRegistry
     * This way, there’s no need for any Controller between the two
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        log.info(">>>>>>> Creating direct mappings between URL and view names ... ");

        registry.addViewController("/login")
                .setViewName("loginPage");

        registry.addViewController("/forgotPassword")
                .setViewName("forgotPassword");

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}///:~