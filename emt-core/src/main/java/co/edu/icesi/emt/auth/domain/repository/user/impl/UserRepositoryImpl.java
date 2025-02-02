package co.edu.icesi.emt.auth.domain.repository.user.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.edu.icesi.emt.auth.domain.model.user.User;
import co.edu.icesi.emt.auth.domain.repository.user.UserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String SCHEMA = "emt";
    private static final String USER_TABLE = "user";
    private static final String USER_FULL_TABLE_NAME = SCHEMA + "." + USER_TABLE;

    private static final String ID = "id";
    private static final String PASSWORD = "password";
    private static final String LAST_LOGIN = "last_login";

    private static final String SELECT_USER_TABLE_COLUMNS = ID + ", " + PASSWORD + ", " + LAST_LOGIN;
    private static final String INSERT_USER_TABLE_COLUMNS = ID + ", " + PASSWORD;

    private static final String SELECT_FROM_USER = "SELECT " + SELECT_USER_TABLE_COLUMNS + " FROM "
            + USER_FULL_TABLE_NAME;
    private static final String SELECT_FROM_USER_WHERE_USERNAME = SELECT_FROM_USER + " WHERE " + ID + " = ?";

    private static final String INSERT_INTO_USER = "INSERT INTO " + USER_FULL_TABLE_NAME + " ("
            + INSERT_USER_TABLE_COLUMNS + ") VALUES ( ?, ?)";

    private static final String DELETE_FROM_USER_WHERE_USERNAME = "DELETE FROM " + USER_FULL_TABLE_NAME + " WHERE "
            + ID
            + " = ?";

    private static final String UPDATE_USER_PASSWORD = "UPDATE " + USER_FULL_TABLE_NAME + " SET " + PASSWORD
            + " = ? WHERE " + ID + " = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(String username, String password) {
        jdbcTemplate.update(INSERT_INTO_USER, new Object[] { username, password });
    }

    @Override
    public User findByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(SELECT_FROM_USER_WHERE_USERNAME,
                    this::parse, new Object[] { username });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return jdbcTemplate.query(SELECT_FROM_USER,
                    this::parse);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteByUsername(String username) {
        jdbcTemplate.update(DELETE_FROM_USER_WHERE_USERNAME, new Object[] { username });
    }

    private User parse(final ResultSet rs, final int rowNum) throws SQLException {
        String username = rs.getString(ID);
        String password = rs.getString(PASSWORD);
        Instant lastLogin = rs.getTimestamp(LAST_LOGIN).toInstant();

        return new User(username, password, lastLogin);
    }

    @Override
    public void changePassword(String username, String password) {
        jdbcTemplate.update(UPDATE_USER_PASSWORD, new Object[] { password, username });
    }
}
