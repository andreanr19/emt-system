package co.edu.icesi.emt.auth.application.service.user;

import java.util.List;
import java.util.Set;

import co.edu.icesi.emt.auth.domain.model.role.Role;
import co.edu.icesi.emt.auth.domain.model.user.User;

public interface UserService {
    void save(String username, String password);

    User findByUsername(String username);

    List<User> findAll();

    void deleteByUsername(String username);

    Set<Role> findUserRolesByUsername(String username);

    void changePassword(String username, String password);
}