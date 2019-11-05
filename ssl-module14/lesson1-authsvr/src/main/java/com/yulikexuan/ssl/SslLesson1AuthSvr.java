//: com.yulikexuan.ssl.SslLesson1AuthSvr.java


package com.yulikexuan.ssl;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebMvc
@SpringBootApplication
public class SslLesson1AuthSvr {

    public static void main(String[] args) {
        SpringApplication.run(SslLesson1AuthSvr.class, args);
    }

}///:~