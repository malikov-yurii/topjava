package ru.javawebinar.topjava.web.user;

import org.junit.Test;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.Profiles.DB_IMPLEMENTATION;
import static ru.javawebinar.topjava.Profiles.HEROKU;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles({HEROKU, DB_IMPLEMENTATION})
public class HerokuRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestController.REST_URL + '/';

    // Set DATABASE_URL environment for heroku profile
    static {
        Resource resource = new ClassPathResource("db/postgres.properties");
        try {
            PropertySource propertySource = new ResourcePropertySource(resource);
            String herokuDbUrl = String.format("postgres://%s:%s@%s",
                    propertySource.getProperty("database.username"),
                    propertySource.getProperty("database.password"),
                    ((String) propertySource.getProperty("database.url")).substring(18));
            System.out.println(herokuDbUrl);

            System.setProperty("DATABASE_URL", herokuDbUrl);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + USER_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testUpdate() throws Exception {
        mockMvc.perform(put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(USER)))
                .andExpect(status().is5xxServerError());
    }
}