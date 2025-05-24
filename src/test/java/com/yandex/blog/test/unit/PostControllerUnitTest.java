package com.yandex.blog.test.unit;

import com.yandex.blog.controllers.PostController;
import com.yandex.blog.model.Post;
import com.yandex.blog.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostControllerUnitTest {

    @Mock
    private PostService postService;

    @Mock
    private Model model;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPost_shouldAddPostAndReturnView() {
        Post post = new Post(1L, "title1", "shortDesc1", "content1", 0, null);

        when(postService.findById(1L)).thenReturn(post);
        String viewName = postController.getPost(1L, model);

        assertEquals("post", viewName);
        verify(model, times(1)).addAttribute("post", post);
        verify(postService, times(1)).findById(1L);
    }

    @Test
    void deletePost_shouldDeletePostAndRedirect() {
        String viewName = postController.deletePost(1L);

        assertEquals("redirect:/feed", viewName);
        verify(postService, times(1)).deleteById(1L);
    }

    @Test
    void createPost_shouldSavePostAndRedirect() {
        Post post = new Post(1L, "title1", "shortDesc1", "content1", 0, null);

        String viewName = postController.createPost(post, null, "tag");

        assertEquals("redirect:/feed", viewName);
        verify(postService, times(1)).save(post, null);
    }

    @Test
    void showCreatePost_shouldAddEmptyPostAndReturnView() {
        String viewName = postController.showCreatePost(model);

        assertEquals("createPost", viewName);
        verify(model, times(1)).addAttribute(eq("post"), any(Post.class));
    }

    @Test
    void showEditPostForm_shouldReturnView() {
        Post post = new Post(1L, "title1", "shortDesc1", "content1", 0, null);
        when(postService.findById(1L)).thenReturn(post);

        String viewName = postController.showEditPostForm(1L, model);

        assertEquals("editPost", viewName);
        verify(model, times(1)).addAttribute(eq("post"), any(Post.class));
    }


    @Test
    void editPost_ShouldUpdatePostAndRedirect() {
        Post post = new Post(1L, "title1", "shortDesc1", "content1", 0, null);

        String viewName = postController.editPost(1L, post, "tag", null, false);
        assertEquals("redirect:/post/1", viewName);
        verify(postService, times(1)).update(post, null, false);
    }

    @Test
    void likePost_shouldIncrementLikesAndReturnView() {
        Post post = new Post(1L, "title1", "shortDesc1", "content1", 0, null);

        String viewName = postController.likePost(1L, post);
        assertEquals("redirect:/post/1", viewName);
        verify(postService, times(1)).incrementLikes(post);
    }
}
