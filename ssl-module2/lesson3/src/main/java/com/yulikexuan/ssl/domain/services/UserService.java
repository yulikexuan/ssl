//: com.yulikexuan.ssl.domain.services.UserService.java


package com.yulikexuan.ssl.domain.services;


import com.yulikexuan.ssl.domain.model.User;
import com.yulikexuan.ssl.domain.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
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
        User savedUser = Optional.ofNullable(user)
                .map(this.userRepository::save)
                .orElseThrow(RuntimeException::new);
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
    public Optional<User> findUserByEmail(String email) {
        return this.userRepository.findByEmail(email).stream().findAny();
    }

}///:~