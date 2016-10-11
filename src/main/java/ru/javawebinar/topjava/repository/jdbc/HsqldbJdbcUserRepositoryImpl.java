package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Profile(Profiles.HSQLDB)
public class HsqldbJdbcUserRepositoryImpl extends JdbcUserRepositoryImpl {

    public HsqldbJdbcUserRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public User save(User user) {
        return save(user, getSqlParameterSourceMapWithoutRegistered(user).addValue("registered", new Timestamp(user.getRegistered().getTime())));
    }

    @Override
    public boolean delete(int id) {
        return super.delete(id);
    }

    @Override
    public User get(int id) {
        return super.get(id);
    }

    @Override
    public User getByEmail(String email) {
        return super.getByEmail(email);
    }

    @Override
    public List<User> getAll() {
        return super.getAll();
    }
}
