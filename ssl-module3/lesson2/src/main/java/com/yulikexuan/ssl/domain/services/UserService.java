//: com.yulikexuan.ssl.domain.services.UserService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.SecurityQuestion;
import com.yulikexuan.ssl.domain.model.SecurityQuestionDefinition;
import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    private final ISecurityQuestionService securityQuestionService;
    private final ISecurityQuestionDefinitionService secuQuestionDefService;
    private final IPasswordResetTokenService passwordResetTokenService;

    @Autowired
    public UserService(IUserRepository userRepository,
                       ISecurityQuestionService securityQuestionService,
                       ISecurityQuestionDefinitionService secuQuestionDefService,
                       IPasswordResetTokenService passwordResetTokenService) {

        this.userRepository = userRepository;
        this.securityQuestionService = securityQuestionService;
        this.secuQuestionDefService = secuQuestionDefService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @Override
    public long count() {
        return this.userRepository.count();
    }

    @Override
    public List<User> findAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {

        if (user == null) {
            throw new RuntimeException("User to be saved is null!");
        }

        User savedUser = this.userRepository.save(user);

        return savedUser;
    }

    @Override
    public User saveUser(User user, Long securityQuestionId, String answer) {

        final User savedUser = this.saveUser(user);

        this.secuQuestionDefService.findById(securityQuestionId)
                .ifPresent(def -> saveSecurityQuestionForUser(savedUser, def, answer));

        return savedUser;
    }

    private void saveSecurityQuestionForUser(
            final User user, final SecurityQuestionDefinition def,
            final String answer) {

        SecurityQuestion securityQuestion = SecurityQuestion.builder()
                .user(user)
                .answer(answer)
                .questionDefinition(def)
                .build();
        this.securityQuestionService.save(securityQuestion);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return this.userRepository.findByUsername(username).stream().findAny();
    }

    @Override
    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public void changeUserPassword(final User user, String password) {
        user.setPassword(password);
        this.userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return this.userRepository.findByEmail(email).stream().findAny();
    }

    @Override
    public boolean isSecurityQuestionAnswerCorrect(User user, String answer) {

        return this.securityQuestionService.findByUserAndAnswer(user, answer)
                .isPresent();
    }

    @Override
    public Long getSecurityQuestionDefinitionIdByUser(User user) {
        return this.securityQuestionService.findByUser(user)
                .map(sq -> sq.getQuestionDefinition().getId())
                .orElse(1L);
    }

}///:~