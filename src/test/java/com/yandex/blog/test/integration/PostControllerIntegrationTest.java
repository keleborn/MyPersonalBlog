package com.yandex.blog.test.integration;

import com.yandex.blog.model.Comment;
import com.yandex.blog.model.Post;
import com.yandex.blog.repository.CommentRepository;
import com.yandex.blog.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("alter table posts alter column id restart with 1");
        jdbcTemplate.execute("alter table comments alter column id restart with 1");
        jdbcTemplate.execute("insert into posts(title, short_description, content) values ('Title1', 'ShorDesc1', 'Content1')");
    }

    @Test
    void getPost_shouldReturnHtmlWithPost() throws Exception {
        mockMvc.perform(get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("//a[@href='/feed']").exists())
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

        Optional<Post> savedPost = postRepository.findById(2L);

        assertThat(savedPost).isPresent();
        assertThat(savedPost.get().getTitle()).isEqualTo("Title2");
    }

    @Test
    void showCreatePost_shouldReturnCreatePostHtml() throws Exception {
        mockMvc.perform(get("/post/new"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("createPost"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("//a[@href='/feed']").exists())
                .andExpect(xpath("//form[@action='/post/new']//button[text()='Создать']").exists());
    }

    @Test
    void deletePost_shouldDeletePostFromDatabaseAndRedirect() throws Exception {
        mockMvc.perform(post("/post/delete/1")
                .param("method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));

        assertThat(postRepository.findById(1L)).isEmpty();
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

        Optional<Post> updatedPost = postRepository.findById(1L);

        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getTitle()).isEqualTo("Title2Updated");
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

        Optional<Post> post = postRepository.findById(1L);

        assertThat(post).isPresent();
        assertThat(post.get().getLikes()).isEqualTo(1);
    }

    @Test
    void addComment_shouldAddCommentToDatabase() throws Exception {
        mockMvc.perform(post("/post/comments")
                        .param("postId", "1")
                        .param("content", "content"))
                .andExpect(status().isOk());

        assertThat(commentRepository.countByPostId(1L)).isEqualTo(1);
    }

    @Test
    void editComment_shouldEditCommentInDatabase() throws Exception {
        commentRepository.save(new Comment(null, 1L, "content"));
        mockMvc.perform(post("/post/comments/edit")
                        .param("id", "1")
                        .param("content", "updatedContent"))
                .andExpect(status().isOk());

        Optional<Comment> comments = commentRepository.findById(1L);

        assertThat(comments).isPresent();
        assertThat(comments.get().getPostId()).isEqualTo(1L);
        assertThat(comments.get().getContent()).isEqualTo("updatedContent");
    }

    @Test
    void deleteComment_shouldDeleteCommentFromDatabase() throws Exception {
        commentRepository.save(new Comment(null, 1L, "content"));
        mockMvc.perform(post("/post/comments/delete")
                        .param("id", "1"))
                .andExpect(status().isOk());

        assertThat(commentRepository.countByPostId(1L)).isEqualTo(0);
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
