package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
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
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert userInsert;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SimpleJdbcInsert userRoleInsert;

    @Autowired
    public JdbcUserRepositoryImpl(DataSource dataSource) {
        this.userInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("id");
        this.userRoleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("USER_ROLES");
    }

    @Override
    @Transactional
    public User save(User user) {
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
            insertRoles(user);
        } else {
            deleteRoles(user);
            insertRoles(user);
            namedParameterJdbcTemplate.update(
                    "UPDATE users SET name=:name, email=:email, password=:password, " +
                            "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", userMap);
        }
        return user;
    }

    public void insertRoles(User u){
        Set<Role> roles = u.getRoles();
        Iterator<Role> iterator = roles.iterator();
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) VALUES (?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, u.getId());
                ps.setString(2, iterator.next().name());
            }

            @Override
            public int getBatchSize() {
                return roles.size();
            }
        });
    }

    public void deleteRoles(User u){
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", u.getId());
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT u.*, string_agg(ur.role, ',') AS all_roles " +
                        "FROM users AS u " +
                        "JOIN user_roles AS ur ON u.id = ur.user_id " +
                        "WHERE id=? " +
                        "GROUP BY u.id",
                USER_EXTRACTOR, id);
        return users != null ? DataAccessUtils.singleResult(users) : null;
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query(
                "SELECT u.*, string_agg(ur.role, ',') AS all_roles " +
                        "FROM users AS u " +
                        "JOIN user_roles AS ur ON u.id = ur.user_id " +
                        "WHERE u.email = ? " +
                        "GROUP BY u.id",
                USER_EXTRACTOR, email);
        return users != null ? DataAccessUtils.singleResult(users) : null;
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(
                "SELECT u.*, string_agg(ur.role, ',') AS all_roles " +
                        "FROM users AS u " +
                        "JOIN user_roles AS ur ON u.id = ur.user_id " +
                        "GROUP BY u.id " +
                        "ORDER BY u.name, u.email",
                USER_EXTRACTOR);
    }
}
