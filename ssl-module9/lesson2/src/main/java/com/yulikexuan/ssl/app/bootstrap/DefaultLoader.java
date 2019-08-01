//: com.yulikexuan.ssl.app.bootstrap.DefaultLoader.java


package com.yulikexuan.ssl.app.bootstrap;


import com.yulikexuan.ssl.app.config.security.SslBasicSecurityConfigerAdapterWithComments;
import com.yulikexuan.ssl.domain.model.Privilege;
import com.yulikexuan.ssl.domain.model.Role;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.services.IPrivilegeService;
import com.yulikexuan.ssl.domain.services.IRoleService;
import com.yulikexuan.ssl.domain.services.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;


@Slf4j
@Component
public class DefaultLoader implements CommandLineRunner {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;
    private final IPrivilegeService privilegeService;

    @Autowired
    public DefaultLoader(IUserService userService,
                         PasswordEncoder passwordEncoder,
                         IRoleService roleService,
                         IPrivilegeService privilegeService) {

        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
        this.privilegeService = privilegeService;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        loadUsers();
    }

    private void loadUsers() {

        Privilege readPrivilege = Privilege.builder().name("READ_PRIVILEGE")
                .build();
        this.privilegeService.savePrivilege(readPrivilege);

        Privilege writePrivilege = Privilege.builder().name("WRITE_PRIVILEGE")
                .build();
        this.privilegeService.savePrivilege(writePrivilege);

        Role roleAdmin = Role.builder().name("ROLE_ADMIN")
                .privileges(Set.of(readPrivilege, writePrivilege))
                .build();
        this.roleService.saveRole(roleAdmin);

        Role roleUser = Role.builder().name("ROLE_USER")
                .privileges(Set.of(readPrivilege))
                .build();
        this.roleService.saveRole(roleUser);

        String pw = this.passwordEncoder.encode(
                SslBasicSecurityConfigerAdapterWithComments.DEFAULT_SIMPLE_PW);
        Long questionId = 5L;
        String securityQuestionAnswer = "Zhengzhou";

        this.userService.saveUser(User.builder()
                .username("admin")
                .email("yulikexuan@gmail.com")
                .password(pw)
                .enabled(true)
                .roles(Set.of(roleAdmin))
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userService.saveUser(User.builder()
                .username("yul")
                .email("yu.li@tecsys.com")
                .password(pw)
                .enabled(true)
                .roles(Set.of(roleUser))
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userService.saveUser(User.builder()
                .username("Bill Gates")
                .email("billgates@microsoft.com")
                .password(pw)
                .enabled(true)
                .roles(Set.of(roleUser))
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userService.saveUser(User.builder()
                .username("Steve Jobs")
                .email("stevejobs@apple.com")
                .password(pw)
                .enabled(true)
                .roles(Set.of(roleUser))
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userService.saveUser(User.builder()
                .username("Donald Trump")
                .email("donaldtrump@usa.com")
                .password(pw)
                .enabled(true)
                .roles(Set.of(roleUser))
                .created(Timestamp.from(Instant.now()))
                .build());

        this.userService.saveUser(User.builder()
                .username("Mike Pence")
                .email("mikepence@usa.com")
                .password(pw)
                .enabled(true)
                .roles(Set.of(roleUser))
                .created(Timestamp.from(Instant.now()))
                .build());

        log.info(">>>>>>> {} users Loaded. ",
                this.userService.count());
    }

}///:~