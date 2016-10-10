package ru.javawebinar.topjava.service.DatajpaServiceTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserServiceTest;

import java.util.List;

import static ru.javawebinar.topjava.UserTestData.MATCHER;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest{

    @Autowired
    protected MealService mealService;

    @Test
    public void testGetUserWithAllMeals() throws Exception {
        User userActual = userService.getUserWithAllMeals(USER_ID);
        User userExpected = new User(USER);
        userExpected.setMeals((List<Meal>)(mealService.getAll(USER_ID)));
        MATCHER.assertEquals(userExpected, userActual);
    }
}
