package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile(Profiles.HSQLDB)
public class HsqldbJdbcMealRepositoryImpl extends JdbcMealRepositoryImpl<Timestamp>{
    public HsqldbJdbcMealRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Timestamp getProperDateTime(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }
}
