package com.yandex.blog.test.integration;

import com.yandex.blog.WebConfiguration;
import com.yandex.blog.configuration.DataSourceConfiguration;
import com.yandex.blog.model.Comment;
import com.yandex.blog.model.Post;
import com.yandex.blog.repository.CommentRepository;
import com.yandex.blog.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("alter table posts alter column id restart with 1");
        jdbcTemplate.execute("alter table comments alter column id restart with 1");
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
        InputStream is = getClass().getClassLoader().getResourceAsStream("images/test.png");
        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", is);

        mockMvc.perform(multipart("/post/new")
                        .file(file)
                        .param("title","Title2")
                        .param("shortDescription","ShortDesc2")
                        .param("content","Content2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));

        Post savedPost = postRepository.findById(2L);
        assertEquals("Title2", savedPost.getTitle());
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

        assertEquals(0,postRepository.findAll().size());
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

        Post updatedPost = postRepository.findById(1L);
        assertEquals("Title2Updated", updatedPost.getTitle());
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
    void likePost_shouldIncrementLikesAndRedirectToPostHtml() throws Exception {
        mockMvc.perform(post("/post/like/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/post/1"));

        assertEquals(1, postRepository.findById(1L).getLikes());
    }

    @Test
    void addComment_shouldAddCommentToDatabase() throws Exception {
        mockMvc.perform(post("/post/comments")
                        .param("postId", "1")
                        .param("content", "content"))
                .andExpect(status().isOk());

        assertEquals(1, commentRepository.countByPostId(1L));
    }

    @Test
    void editComment_shouldEditCommentInDatabase() throws Exception {
        commentRepository.saveComment(new Comment(null, 1L, "content"));
        mockMvc.perform(post("/post/comments/edit")
                        .param("id", "1")
                        .param("content", "updatedContent"))
                .andExpect(status().isOk());

        List<Comment> comments = commentRepository.getCommentsByPostId(1L);

        assertEquals(1, comments.size());
        assertEquals("updatedContent", comments.getFirst().getContent());
    }

    @Test
    void deleteComment_shouldDeleteCommentFromDatabase() throws Exception {
        commentRepository.saveComment(new Comment(null, 1L, "content"));
        mockMvc.perform(post("/post/comments/delete")
                        .param("id", "1"))
                .andExpect(status().isOk());

        assertEquals(0, commentRepository.countByPostId(1L));
    }

    @AfterEach
    void cleanUpUploadedImages() throws Exception {
        Path dir = Paths.get(uploadDir);
        if (Files.exists(dir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path file : stream) {
                    if (Files.isRegularFile(file) && file.getFileName().toString().endsWith("_test.png")) {
                        Files.delete(file);
                    }
                }
            }

        }
    }
}
