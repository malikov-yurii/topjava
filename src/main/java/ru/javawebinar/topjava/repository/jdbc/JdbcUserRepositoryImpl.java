package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: gkislin
 * Date: 26.08.2014
 */

@Repository
public class JdbcUserRepositoryImpl implements UserRepository {

    private static final ResultSetExtractor<List<User>> USER_EXTRACTOR = rs -> {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user1 = new User();
            user1.setId(rs.getInt("id"));
            user1.setName(rs.getString("name"));
            user1.setEmail(rs.getString("email"));
            user1.setPassword(rs.getString("password"));
            user1.setRegistered(rs.getDate("registered"));
            user1.setEnabled(rs.getBoolean("enabled"));
            user1.setCaloriesPerDay(rs.getInt("calories_per_day"));
            user1.setRoles(Arrays.stream(rs.getString("all_roles").split(",")).map(Role::valueOf).collect(Collectors.toSet()));
            users.add(user1);
        }
        return users;
    };
    @Autowired
    private DataSourceTransactionManager transactionManager;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private TransactionStatus txStatus;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleJdbcInsert userInsert;

    private SimpleJdbcInsert userRolesInsert;

    @Autowired
    public JdbcUserRepositoryImpl(DataSource dataSource) {
        this.userInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("id");

        this.userRolesInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("USER_ROLES");
    }

    @Override
    public User save(User user) {
//        txStatus = transactionManager.getTransaction(txDef);
//        try {
        MapSqlParameterSource userMap = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("registered", user.getRegistered())
                .addValue("enabled", user.isEnabled())
                .addValue("caloriesPerDay", user.getCaloriesPerDay());

        if (user.isNew()) {
            Number newKey = userInsert.executeAndReturnKey(userMap);
            user.setId(newKey.intValue());
        } else {
            namedParameterJdbcTemplate.update(
                    "UPDATE users SET name=:name, email=:email, password=:password, " +
                            "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", userMap);
        }
        for (Role role : user.getRoles()) {
            MapSqlParameterSource userRolesMap = new MapSqlParameterSource()
                    .addValue("user_id", user.getId())
                    .addValue("role", role);
            try {
                userRolesInsert.execute(userRolesMap);
            } catch (DuplicateKeyException e) {
            }
        }
//            transactionManager.commit(txStatus);
//        } catch (Exception e) {
//            transactionManager.rollback(txStatus);
//            throw e;
//        }
        return user;
    }

    @Override
    public boolean delete(int id) {
        txStatus = transactionManager.getTransaction(txDef);
        Boolean isSuccessfulDeleteOperation;
        try {
            isSuccessfulDeleteOperation = jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
        return isSuccessfulDeleteOperation;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT users.*, string_agg(user_roles.role, ',') AS all_roles " +
                        "FROM users " +
                        "JOIN user_roles ON users.id = user_roles.user_id " +
                        "WHERE id=? " +
                        "GROUP BY users.id",
                USER_EXTRACTOR, id);
        return users != null ? DataAccessUtils.singleResult(users) : null;
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query(
                "SELECT users.*, string_agg(user_roles.role, ',') AS all_roles " +
                        "FROM users " +
                        "JOIN user_roles ON users.id = user_roles.user_id " +
                        "WHERE users.email = ? " +
                        "GROUP BY users.id",
                USER_EXTRACTOR, email);
        return users != null ? DataAccessUtils.singleResult(users) : null;
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(
                "SELECT users.*, string_agg(user_roles.role, ',') AS all_roles " +
                        "FROM users JOIN user_roles ON users.id = user_roles.user_id " +
                        "GROUP BY users.id " +
                        "ORDER BY name, email ",
                USER_EXTRACTOR);
    }
}
