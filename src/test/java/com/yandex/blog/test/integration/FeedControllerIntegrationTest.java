package com.yandex.blog.test.integration;

import com.yandex.blog.WebConfiguration;
import com.yandex.blog.configuration.DataSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class FeedControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("alter table posts alter column id restart with 1");
        jdbcTemplate.execute("insert into posts(title, shortDescription, content) values ('Title1', 'ShorDesc1', 'Content1')");
        jdbcTemplate.execute("insert into posts(title, shortDescription, content) values ('Title2', 'ShorDesc2', 'Content2')");
        jdbcTemplate.execute("insert into posts(title, shortDescription, content) values ('Title3', 'ShorDesc3', 'Content3')");
        jdbcTemplate.execute("insert into posts(title, shortDescription, content) values ('Title4', 'ShorDesc4', 'Content4')");
        jdbcTemplate.execute("insert into posts(title, shortDescription, content) values ('Title5', 'ShorDesc5', 'Content5')");
        jdbcTemplate.execute("insert into posts(title, shortDescription, content) values ('Title6', 'ShorDesc6', 'Content6')");
    }

    @Test
    void getPosts_withDefaultPagination_ShouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", hasSize(5)));
    }

    @Test
    void getPosts_withPaginationLimit10_ShouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/feed?limit=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", hasSize(6)));
    }

    @Test
    void getPosts_withPaginationLimit5AndPage2_ShouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/feed?limit=5&page=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", hasSize(1)));
    }

    @Test
    void getPosts_withPaginationLimit5AndPage2AndTagFilter_ShouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/feed?tags=tag&limit=5&page=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("selectedTags"))
                .andExpect(model().attribute("posts", hasSize(1)));
    }
}
