package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;

import javax.sql.DataSource;

@Repository
@Profile(Profiles.POSTGRES)
public class PostgresJdbcUserRepositoryImpl extends  JdbcUserRepositoryImpl {

    public PostgresJdbcUserRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

}
