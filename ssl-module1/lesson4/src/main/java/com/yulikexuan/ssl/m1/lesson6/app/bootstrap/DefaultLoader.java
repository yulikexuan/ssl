//: com.yulikexuan.ssl.m1.lesson6.app.bootstrap.DefaultLoader.java


package com.yulikexuan.ssl.m1.lesson6.app.bootstrap;


import com.yulikexuan.ssl.m1.lesson6.domain.model.User;
import com.yulikexuan.ssl.m1.lesson6.domain.repositories.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;


@Slf4j
@Component
public class DefaultLoader implements CommandLineRunner {

    private final IUserRepository userRepository;

    @Autowired
    public DefaultLoader(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        this.userRepository.save(User.builder()
                .username("Bill Gates")
                .email("billgates@microsoft.com")
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userRepository.save(User.builder()
                .username("Steve Jobs")
                .email("stevejobs@apple.com")
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userRepository.save(User.builder()
                .username("Donald Trump")
                .email("donaldtrump@usa.com")
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userRepository.save(User.builder()
                .username("Mike Pence")
                .email("mikepence@usa.com")
                .created(Timestamp.from(Instant.now()))
                .build());

        log.info(">>>>>>> {} users Loaded. ",
                this.userRepository.count());
    }

}///:~