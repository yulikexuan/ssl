//: com.yulikexuan.ssl.app.bootstrap.DefaultLoader.java


package com.yulikexuan.ssl.app.bootstrap;


import com.yulikexuan.ssl.app.config.security.SslSecurityConfig;
import com.yulikexuan.ssl.domain.model.SecurityQuestionDefinition;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.repositories.ISecurityQuestionDefinitionRepository;
import com.yulikexuan.ssl.domain.services.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;


@Slf4j
// This Loader is not for this lesson  // @Component
public class DefaultLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    private final IUserService userService;
    private final ISecurityQuestionDefinitionRepository secuQuestionDefRepository;

    @Autowired
    public DefaultLoader(PasswordEncoder passwordEncoder,
                         IUserService userService,
                         ISecurityQuestionDefinitionRepository
                                 secuQuestionDefRepository) {

        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.secuQuestionDefRepository = secuQuestionDefRepository;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        loadSecurityQuestionDefinitions();
        loadUsers();
    }

    private void loadUsers() {

        String pw = this.passwordEncoder.encode(
                SslSecurityConfig.DEFAULT_SIMPLE_PW);
        Long questionId = 5L;
        String securityQuestionAnswer = "Zhengzhou";

        this.userService.saveUser(User.builder()
                .username("admin")
                .email("yulikexuan@gmail.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build(), questionId, securityQuestionAnswer);

        this.userService.saveUser(User.builder()
                .username("yul")
                .email("yu.li@tecsys.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build(), questionId, securityQuestionAnswer);

        this.userService.saveUser(User.builder()
                .username("Bill Gates")
                .email("billgates@microsoft.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build(), questionId, securityQuestionAnswer);

        this.userService.saveUser(User.builder()
                .username("Steve Jobs")
                .email("stevejobs@apple.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build(), questionId, securityQuestionAnswer);

        this.userService.saveUser(User.builder()
                .username("Donald Trump")
                .email("donaldtrump@usa.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build(), questionId, securityQuestionAnswer);

        this.userService.saveUser(User.builder()
                .username("Mike Pence")
                .email("mikepence@usa.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build(), questionId, securityQuestionAnswer);

        log.info(">>>>>>> {} users Loaded. ",
                this.userService.count());
    }

    private void loadSecurityQuestionDefinitions() {

        this.secuQuestionDefRepository.save(
                SecurityQuestionDefinition.builder()
                        .id(1L)
                        .text("What is the last name of the teacher who gave you your first failing grade?")
                        .build());

        this.secuQuestionDefRepository.save(
                SecurityQuestionDefinition.builder()
                        .id(2L)
                        .text("What is the first name of the person you first kissed?")
                        .build());

        this.secuQuestionDefRepository.save(
                SecurityQuestionDefinition.builder()
                        .id(3L)
                        .text("What is the name of the place your wedding reception was held?")
                        .build());

        this.secuQuestionDefRepository.save(
                SecurityQuestionDefinition.builder()
                        .id(4L)
                        .text("When you were young, what did you want to be when you grew up?")
                        .build());

        this.secuQuestionDefRepository.save(
                SecurityQuestionDefinition.builder()
                        .id(5L)
                        .text("Where were you New Year''s 2000?")
                        .build());

        this.secuQuestionDefRepository.save(
                SecurityQuestionDefinition.builder()
                        .id(6L)
                        .text("Who was your childhood hero?")
                        .build());

        log.info(">>>>>>> {} security question definitions Loaded. ",
                this.secuQuestionDefRepository.count());
    }

}///:~