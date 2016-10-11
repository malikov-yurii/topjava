package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Repository
@Profile(Profiles.POSTGRES)
public class PostgresJdbcMealRepositoryImpl extends JdbcMealRepositoryImpl {

    public PostgresJdbcMealRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public LocalDateTime getDateTime(Meal meal) {
        return meal.getDateTime();
    }
}
