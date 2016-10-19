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
    public JdbcUserRepositoryImpl(DataSource dataSource) {
        this.userInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    @Transactional
    public User save(User user) {
        StringBuilder sql = new StringBuilder();

        if (user.isNew()) {
            MapSqlParameterSource userMap = new MapSqlParameterSource()
                    .addValue("id", user.getId())
                    .addValue("name", user.getName())
                    .addValue("email", user.getEmail())
                    .addValue("password", user.getPassword())
                    .addValue("registered", user.getRegistered())
                    .addValue("enabled", user.isEnabled())
                    .addValue("caloriesPerDay", user.getCaloriesPerDay());
            Number newKey = userInsert.executeAndReturnKey(userMap);
            user.setId(newKey.intValue());
        } else {
            sql.append(String.format("UPDATE users SET name=\'%s\', email=\'%s\', password=\'%s\', registered=\'%s\'," +
                            " enabled=\'%b\', calories_per_day=%d WHERE id = %d;",
                    user.getName(), user.getEmail(), user.getPassword(), new java.sql.Date(user.getRegistered().getTime()),
                    user.isEnabled(), user.getCaloriesPerDay(), user.getId()));
            sql.append("DELETE FROM user_roles WHERE user_id=" + user.getId() + ";");
        }
//            insertBatch(new ArrayList<>(user.getRoles()), newKey.intValue());
        for (Role role : user.getRoles())
            sql.append("INSERT INTO user_roles VALUES (" + user.getId() + ", \'" + role + "\');");

//      All work fine. I just don't know why this weird exception is thrown:
//
//      org.springframework.dao.DataIntegrityViolationException:
//      StatementCallback;
//      SQL [UPDATE users SET name='UpdatedName', email='user@yandex.ru', password='password', registered='2016-10-19',
//      enabled='true', calories_per_day=330 WHERE id = 100000;
//      DELETE FROM user_roles WHERE user_id=100000;
//      INSERT INTO user_roles VALUES (100000, 'ROLE_USER');];
//      Batch entry 1 <unknown> was aborted: Too many update results were returned.
//      Call getNextException to see other errors in the batch.;
//      nested exception is java.sql.BatchUpdateException: Batch entry 1 <unknown> was aborted:
//      Too many update results were returned.  Call getNextException to see other errors in the batch.
        try {
            jdbcTemplate.batchUpdate(new String[]{sql.toString()});
        } catch (DataIntegrityViolationException e) {
        }

        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        Boolean isSuccessfulDeleteOperation;
        isSuccessfulDeleteOperation = jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
        return isSuccessfulDeleteOperation;
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
    /*
        public void insertBatch(final List<Role> roleList, int id) {
            String sql = "INSERT INTO user_roles VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    String roleString = roleList.get(i).toString();
                    ps.setInt(1, id);
                    ps.setString(2, roleString);
                }

                @Override
                public int getBatchSize() {
                    return roleList.size();
                }
            });
        }
    */
}
