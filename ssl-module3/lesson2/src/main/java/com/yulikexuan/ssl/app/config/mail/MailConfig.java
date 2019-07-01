//: com.yulikexuan.ssl.app.config.mail.MailConfig.java


package com.yulikexuan.ssl.app.config.mail;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


//@Configuration // Use application.yml instead
public class MailConfig {

    @Bean
    public JavaMailSender javaMailService() {

        String emailPw = System.getenv("emailpw");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("yulikexuan@gmail.com");
        mailSender.setPassword(emailPw);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}///:~