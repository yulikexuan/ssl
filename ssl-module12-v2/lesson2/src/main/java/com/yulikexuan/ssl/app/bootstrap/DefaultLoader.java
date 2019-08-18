//: com.yulikexuan.ssl.app.bootstrap.DefaultLoader.java


package com.yulikexuan.ssl.app.bootstrap;


import com.yulikexuan.ssl.app.config.security.SslSecurityConfigerAdapter;
import com.yulikexuan.ssl.domain.model.Privilege;
import com.yulikexuan.ssl.domain.model.Role;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.model.oauth2.ClientScope;
import com.yulikexuan.ssl.domain.model.oauth2.GrantType;
import com.yulikexuan.ssl.domain.model.oauth2.Client;
import com.yulikexuan.ssl.domain.services.IPrivilegeService;
import com.yulikexuan.ssl.domain.services.IRoleService;
import com.yulikexuan.ssl.domain.services.oauth2.IClientService;
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
    private final IClientService sslClientDetailsService;

    @Autowired
    public DefaultLoader(IUserService userService,
                         PasswordEncoder passwordEncoder,
                         IRoleService roleService,
                         IPrivilegeService privilegeService,
                         IClientService sslClientDetailsService) {

        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
        this.privilegeService = privilegeService;

        this.sslClientDetailsService = sslClientDetailsService;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        this.loadUsers();
        System.out.println("-------------------------------------------------");
        this.loadClients();
    }

    private void loadUsers() {

        Privilege readPrivilege = Privilege.builder().name("PRIVILEGE_READ")
                .build();
        this.privilegeService.savePrivilege(readPrivilege);

        Privilege writePrivilege = Privilege.builder().name("PRIVILEGE_WRITE")
                .build();
        this.privilegeService.savePrivilege(writePrivilege);

        Privilege createPrivilege = Privilege.builder().name("PRIVILEGE_CREATE")
                .build();
        this.privilegeService.savePrivilege(createPrivilege);

        Privilege deletePrivilege = Privilege.builder().name("PRIVILEGE_DELETE")
                .build();
        this.privilegeService.savePrivilege(deletePrivilege);

        Privilege securityPrivilege = Privilege.builder().name("PRIVILEGE_SECURITY")
                .build();
        this.privilegeService.savePrivilege(securityPrivilege);

        Set<Privilege> adminPrivileges = Set.of(
                readPrivilege,
                writePrivilege,
                createPrivilege,
                deletePrivilege);

        Role roleAdmin = Role.builder().name("ROLE_ADMIN")
                .privileges(adminPrivileges)
                .build();
        this.roleService.saveRole(roleAdmin);

        Set<Privilege> userPrivileges = Set.of(
                readPrivilege,
                writePrivilege
        );

        Role roleUser = Role.builder().name("ROLE_USER")
                .privileges(userPrivileges)
                .build();
        this.roleService.saveRole(roleUser);

        Set<Privilege> securityPrivileges = Set.of(
                readPrivilege,
                writePrivilege,
                createPrivilege,
                deletePrivilege,
                securityPrivilege
        );

        Role securityRole = Role.builder()
                .name("ROLE_SECURITY")
                .privileges(securityPrivileges)
                .build();

        this.roleService.saveRole(securityRole);

        String pw = this.passwordEncoder.encode(
                SslSecurityConfigerAdapter.DEFAULT_SIMPLE_PW);

        Long questionId = 5L;

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

        this.userService.saveUser(User.builder()
                .username("guru")
                .email("guru@springframework.com")
                .password(pw)
                .enabled(true)
                .roles(Set.of(securityRole))
                .created(Timestamp.from(Instant.now()))
                .build());

        log.info(">>>>>>> {} users Loaded. ", this.userService.count());

    } // End of loadUsers()

    private void loadClients() {

        ClientScope readScope = ClientScope.builder().scope("PRIVILEGE_READ")
                .build();

        ClientScope writeScope = ClientScope.builder().scope("PRIVILEGE_WRITE")
                .build();

        GrantType pwGrantType = GrantType.builder().type("password").build();

        Client client = Client.builder().clientId("cloud")
                .clientSecret(this.passwordEncoder.encode("2PGlgRk9Mv"))
                .scope(readScope)
                .scope(writeScope)
                .authorizedGrantType(pwGrantType)
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(3600 * 24)
                .autoApprove(true)
                .build();

        this.sslClientDetailsService.save(client);

        log.info(">>>>>>> {} clients loaded.", this.sslClientDetailsService.count());

    }// End of  loadClients()

}///:~