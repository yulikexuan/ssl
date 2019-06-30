//: com.yulikexuan.ssl.app.config.mvc.LssWebMvcConfigurer.java


package com.yulikexuan.ssl.app.config.mvc;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Slf4j
@EnableWebMvc
@Configuration
public class LssWebMvcConfigurer implements WebMvcConfigurer {

    /*
     * "classpath:" is specific to spring
     *   - Sp   ring's resource resolving mechanism
     *     (ie. PathMatchingResourcePatternResolver or other imlementations)
     *     knows about the "classpath:" and "classpath*:" prefixes
     *
     *   - It takes that and resolves into ClassPathResource object(s), which
     *     happen to implement springs Resource interface
     *
     *   - The Resource interface, among other things, has a getInputStream()
     *     method which can be used to get the contents, without having to be
     *     aware of what type of resource it is
     */
    static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/",
            "classpath:/resources/",
            "classpath:/static/",
            "classpath:/public/" };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
         * Add a resource handler for serving static resources based on the
         * specified URL path patterns
         *
         * The handler will be invoked for every incoming request that matches
         * to one of the specified path patterns
         */
        registry.addResourceHandler("/**")
                /*
                 * Add one or more resource locations from which to serve static
                 * content
                 *
                 * Each location must point to a valid directory
                 * Multiple locations may be specified as a comma-separated list
                 * The locations will be checked for a given resource in the
                 * order specified
                 */
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

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