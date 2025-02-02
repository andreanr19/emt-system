package co.edu.icesi.emt.auth.domain.repository.userrole.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import co.edu.icesi.emt.auth.domain.model.role.Role;
import co.edu.icesi.emt.auth.domain.model.user.User;
import co.edu.icesi.emt.auth.domain.repository.userrole.UserRoleRepository;

@SpringBootTest
public class UserRoleRepositoryImplTest {

    @Autowired
    private UserRoleRepository userrole;

    @Test
    void testUserHasRole() {
        assertEquals(false,
                userrole.userHasRole(
                        new User("username", null, null),
                        new Role(null, null)));
    }

    @Test
    void testDeleteUserRoleByUsername() {

    }

    @Test
    void testFindUserRoleIdsByUsername() {

    }

    @Test
    void testSave() {

    }
}
