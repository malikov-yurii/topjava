package ru.javawebinar.topjava.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.BaseEntity;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;

/**
 * User: gkislin
 */
public abstract class AbstractUserController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService service;

    private boolean systemUserForbiddenModification;

    @Autowired
    public void setEnvironment(Environment environment) {
        systemUserForbiddenModification = Arrays.asList(environment.getActiveProfiles()).contains(Profiles.HEROKU);
    }

    public void checkModificationAllowed(Integer id) {
        if (systemUserForbiddenModification && id < BaseEntity.START_SEQ + 2) {
            throw new ValidationException("Admin/User modification is not allowed. <br><br><a class=\"btn btn-primary btn-lg\" role=\"button\" href=\"register\">Register &raquo;</a> your own please.");
        }
    }

    public List<User> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    public User get(int id) {
        log.info("get " + id);
        return service.get(id);
    }

    public User create(User user) {
        user.setId(null);
        log.info("create " + user);
        return service.save(user);
    }

    public void delete(int id) {
        checkModificationAllowed(id);
        log.info("delete " + id);
        service.delete(id);
    }

    public void update(User user, int id) {
        checkModificationAllowed(id);
        user.setId(id);
        log.info("update " + user);
        service.update(user);
    }

    public void update(UserTo userTo) {
        checkModificationAllowed(userTo.getId());
        log.info("update " + userTo);
        service.update(userTo);
    }

    public User getByMail(String email) {
        log.info("getByEmail " + email);
        return service.getByEmail(email);
    }

    public void enable(int id, boolean enabled) {
        checkModificationAllowed(id);
        log.info((enabled ? "enable " : "disable ") + id);
        service.enable(id, enabled);
    }
}
