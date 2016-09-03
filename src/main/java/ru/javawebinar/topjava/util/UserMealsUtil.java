package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

/**
 * GKislin
 * 31.05.2015.
 */
public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
        List<UserMealWithExceed> list = UserMealsUtil.getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000);
        list.forEach(System.out::println);
    }

    public static List<UserMealWithExceed>  getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
//      caloriesPerDaySums collect only day summaries of filtered (startTime,endTime) meals.
//      We need caloriesPerDaySums to exclude redundant calculations.
        Map<LocalDate, Integer> caloriesPerDaySums = new HashMap<>();
        List<UserMealWithExceed> mealListWithExceeded = new ArrayList<>();

        mealList.stream()
                .filter(meal -> TimeUtil.isBetween(meal.getDateTime().toLocalTime(), startTime, endTime))
                .forEach(currentMeal -> {
                    LocalDate mealDate = currentMeal.getDateTime().toLocalDate();
                    if (!(caloriesPerDaySums.containsKey(mealDate))) {
                        int caloriesSum = mealList.stream()
                                .filter(meal -> meal.getDateTime().toLocalDate().equals(mealDate))
                                .mapToInt(meal -> meal.getCalories())
                                .sum();
                        caloriesPerDaySums.put(mealDate, caloriesSum);
                    }
                    boolean exceed = caloriesPerDaySums.get(mealDate) > caloriesPerDay;
                    mealListWithExceeded.add(new UserMealWithExceed(currentMeal.getDateTime(), currentMeal.getDescription(), currentMeal.getCalories(), exceed));
                });

        return mealListWithExceeded;
    }
}
