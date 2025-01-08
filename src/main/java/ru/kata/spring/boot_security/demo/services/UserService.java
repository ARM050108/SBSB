package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    User getUserById(Long id);

    void saveUser(User user);

    void update(long id, User updatedUser, String role);

    void delete(Long id);

    Optional<User> findByUsername(String username);

    void setEncryptedPassword(User user);


    Object findByEmail(String s);
}