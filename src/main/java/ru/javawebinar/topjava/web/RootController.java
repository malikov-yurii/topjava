package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.service.UserServiceImpl;
import ru.javawebinar.topjava.util.TimeUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AbstractUserController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * User: gkislin
 * Date: 22.08.2014
 */
@Controller
public class RootController {
    private static final Logger LOG = LoggerFactory.getLogger(RootController.class);

    @Autowired
    private MealRestController mealController;

    @Autowired
    private AdminRestController userController;

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String setUser(HttpServletRequest request) {
        int userId = Integer.valueOf(request.getParameter("userId"));
        AuthorizedUser.setId(userId);
        return "redirect:meals";
    }

    @RequestMapping(value = "/meals", method = RequestMethod.POST)
    public String mealsPost(HttpServletRequest request) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (request.getParameter("action") == null) {
            final Meal meal = new Meal(
                    LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.valueOf(request.getParameter("calories")));

            if (request.getParameter("id").isEmpty()) {
                LOG.info("Create {}", meal);
                mealController.create(meal);
            } else {
                LOG.info("Update {}", meal);
                mealController.update(meal, getId(request));
            }
            return "redirect:meals";
        }
        return "meals";
    }

    @RequestMapping(value = "/meals", params = {"action=filter"}, method = RequestMethod.POST)
    public String mealsFilterActionPost(HttpServletRequest request) throws ServletException, IOException {
        LocalDate startDate = TimeUtil.parseLocalDate(resetParam("startDate", request));
        LocalDate endDate = TimeUtil.parseLocalDate(resetParam("endDate", request));
        LocalTime startTime = TimeUtil.parseLocalTime(resetParam("startTime", request));
        LocalTime endTime = TimeUtil.parseLocalTime(resetParam("endTime", request));
        request.setAttribute("meals", mealController.getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root() {
        return "index";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String users(Model model) {
        model.addAttribute("users", userController.getAll());
        return "users";
    }

    @RequestMapping(value = "/meals", params = {"action=delete"}, method = RequestMethod.GET)
    public String mealsDeleteActionGet(HttpServletRequest request) throws ServletException, IOException {
        int id = getId(request);
        LOG.info("Delete {}", id);
        mealController.delete(id);
        return "redirect:meals";
    }

    @RequestMapping(value = "/meals", params = {"action=update"}, method = RequestMethod.GET)
    public String mealsUpdateActionGet(HttpServletRequest request) throws ServletException, IOException {
        final Meal meal = mealController.get(getId(request));
        request.setAttribute("meal", meal);
        return "meal";
    }

    @RequestMapping(value = "/meals", params = {"action=create"}, method = RequestMethod.GET)
    public String mealsCreateActionGet(HttpServletRequest request) throws ServletException, IOException {
        final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "", 1000);
        request.setAttribute("meal", meal);
        return "meal";
    }

    @RequestMapping(value = "/meals", method = RequestMethod.GET)
    public String mealsGet(HttpServletRequest request) throws ServletException, IOException {
        if (request.getParameter("action") == null) {
            LOG.info("getAll");
            request.setAttribute("meals", mealController.getAll());
            return "meals";
        }
        return "meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.valueOf(paramId);
    }

    private String resetParam(String param, HttpServletRequest request) {
        String value = request.getParameter(param);
        request.setAttribute(param, value);
        return value;
    }
    
}
