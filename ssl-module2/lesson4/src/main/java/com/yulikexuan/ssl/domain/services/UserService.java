//: com.yulikexuan.ssl.domain.services.UserService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.PasswordResetToken;
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

    private final IPasswordResetTokenService passwordResetTokenService;

    @Autowired
    public UserService(IUserRepository userRepository,
                       IPasswordResetTokenService passwordResetTokenService) {

        this.userRepository = userRepository;
        this.passwordResetTokenService = passwordResetTokenService;
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

}///:~