//: com.yulikexuan.ssl.app.bootstrap.DefaultLoader.java


package com.yulikexuan.ssl.app.bootstrap;


import com.yulikexuan.ssl.app.config.security.LssSecurityConfig;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.repositories.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;


@Slf4j
@Component
public class DefaultLoader implements CommandLineRunner {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultLoader(IUserRepository userRepository,
                         PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        String pw = this.passwordEncoder.encode(
                LssSecurityConfig.DEFAULT_SIMPLE_PW);

        this.userRepository.save(User.builder()
                .username("yul")
                .email("yul@tecsys.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userRepository.save(User.builder()
                .username("Bill Gates")
                .email("billgates@microsoft.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userRepository.save(User.builder()
                .username("Steve Jobs")
                .email("stevejobs@apple.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userRepository.save(User.builder()
                .username("Donald Trump")
                .email("donaldtrump@usa.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userRepository.save(User.builder()
                .username("Mike Pence")
                .email("mikepence@usa.com")
                .password(pw)
                .enabled(true)
                .created(Timestamp.from(Instant.now()))
                .build());

        log.info(">>>>>>> {} users Loaded. ",
                this.userRepository.count());
    }

}///:~