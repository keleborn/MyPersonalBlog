package com.yandex.blog.test.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class FeedControllerIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("insert into posts(title, short_description, content) values ('Title1', 'ShorDesc1', 'Content1')");
        jdbcTemplate.execute("insert into posts(title, short_description, content) values ('Title2', 'ShorDesc2', 'Content2')");
        jdbcTemplate.execute("insert into posts(title, short_description, content) values ('Title3', 'ShorDesc3', 'Content3')");
        jdbcTemplate.execute("insert into posts(title, short_description, content) values ('Title4', 'ShorDesc4', 'Content4')");
        jdbcTemplate.execute("insert into posts(title, short_description, content) values ('Title5', 'ShorDesc5', 'Content5')");
        jdbcTemplate.execute("insert into posts(title, short_description, content) values ('Title6', 'ShorDesc6', 'Content6')");
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
    void getPosts_withPaginationLimit5AndPage1AndTagFilter_ShouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/feed?limit=5&page=1&tags=tag"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("selectedTags"))
                .andExpect(model().attribute("posts", hasSize(0)));
    }
}
