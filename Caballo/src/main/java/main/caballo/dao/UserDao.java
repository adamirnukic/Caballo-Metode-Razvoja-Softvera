package main.caballo.dao;

import main.caballo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByUsername(String username);
    User create(User user);
    List<User> findAll();

    boolean resetPassword(User user);
    boolean deleteById(long id);
}
