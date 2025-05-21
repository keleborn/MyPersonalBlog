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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostControllerIntegrationTest {

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
    }

    @Test
    void getPost_shouldReturnHtmlWithPost() throws Exception {
        mockMvc.perform(get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("//a[@href='/MyPersonalBlog/feed']").exists())
                .andExpect(xpath("//form[@action='/post/delete/1' and @method='post']").exists())
                .andExpect(xpath("//form[@action='/post/delete/1']//button[@type='submit']").exists())
                .andExpect(xpath("//form[@action='/post/like/1' and @method='post']").exists())
                .andExpect(xpath("//form[@action='/post/like/1']//button[@type='submit']").exists());
    }

    @Test
    void createPost_shouldAddPostToDatabaseAndRedirect() throws Exception {
        mockMvc.perform(post("/post/new")
                        .param("title","Title2")
                        .param("shortDescription","ShortDesc2")
                        .param("content","Content2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));
    }

    @Test
    void showCreatePost_shouldReturnCreatePostHtml() throws Exception {
        mockMvc.perform(get("/post/new"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("createPost"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("//a[@href='/MyPersonalBlog/feed']").exists())
                .andExpect(xpath("//form[@action='/post/new']//button[text()='Создать']").exists());
    }

    @Test
    void deletePost_shouldDeletePostFromDatabaseAndRedirect() throws Exception {
        mockMvc.perform(post("/post/delete/1")
                .param("method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));
    }

    @Test
    void editPost_shouldAddPostToDatabaseAndRedirect() throws Exception {
        mockMvc.perform(post("/post/edit/1")
                        .param("title","Title2Updated")
                        .param("shortDescription","ShortDesc2Updated")
                        .param("content","Content2Updated")
                        .param("tags", "tagsUpdated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/1"));
    }

    @Test
    void showEditPost_shouldReturnEditPostHtml() throws Exception {
        mockMvc.perform(get("/post/edit/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("editPost"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void likePost_shouldRedirectToPostHtml() throws Exception {
        mockMvc.perform(post("/post/like/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/post/1"));
    }
}
