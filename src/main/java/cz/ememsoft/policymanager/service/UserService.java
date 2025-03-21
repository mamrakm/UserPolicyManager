package cz.ememsoft.policymanager.service;

import cz.ememsoft.policymanager.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserByName(String name);

    User createUser(User user);

    User updateUser(String name, User user);

    void deleteUser(String name);
}