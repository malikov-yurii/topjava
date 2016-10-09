package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GKislin
 * 27.03.2015.
 */
@Repository
public class DataJpaMealRepositoryImpl implements MealRepository {

    @Autowired
    private CrudMealRepository crudRepository;

    @Override
    public Meal save(Meal meal, int userId) {
        if (!meal.isNew())
            if (!meal.isNew() && get(meal.getId(), userId) == null) {
                return null;
            }
        return crudRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        if (get(id, userId) != null)
            return crudRepository.delete(id) != 0;
        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = crudRepository.getOne(id);
        if (meal.getUser().getId() == userId)
            return meal;
        return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAll(userId);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return crudRepository.getBetween(startDate, endDate, userId);
    }
}
