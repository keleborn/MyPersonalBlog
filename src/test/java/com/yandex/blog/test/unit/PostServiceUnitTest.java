package com.yandex.blog.test.unit;


import com.yandex.blog.model.Post;
import com.yandex.blog.repository.CommentRepository;
import com.yandex.blog.repository.PostRepository;
import com.yandex.blog.repository.TagRepository;
import com.yandex.blog.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostServiceUnitTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_shouldReturnPostWhenExists() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0, null);

        when(postRepository.findById(1L)).thenReturn(post);
        Post result = postService.findById(1L);
        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getContent(), result.getContent());
        assertEquals(post.getShortDescription(), result.getShortDescription());

        verify(tagRepository, times(1)).findAllTagsByPostId(post.getId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldReturnNullWhenPostDoesNotExist() {
        when(postRepository.findById(1L)).thenReturn(null);

        postService.findById(1L);

        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void save_shouldSavePost() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        Post post = new Post(1L, "title1", "desc1", "content1", 0, null);

        postService.save(post, file);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void deleteById_shouldDeletePost() {
        postService.deleteById(1L);
        verify(postRepository, times(1)).deleteById(1L);
        verify(tagRepository, times(1)).deleteTagsForPost(1L);
    }

    @Test
    void splitToParagraphs_shouldSplitIntoParagraphs() {
        String content1 = "Asd. Bcd. Dcd. Qwe.";
        String content2 = "Qwer. Aok.\n Dasd. Dadd. Add. Ffghh.";
        List<String> result1 = postService.splitContentToParagraphs(content1);
        List<String> result2 = postService.splitContentToParagraphs(content2);
        assertEquals(2, result1.size());
        assertEquals(2, result2.size());
    }

    @Test
    void update_shouldUpdatePost() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0, null);

        when(postRepository.findById(1L)).thenReturn(post);

        postService.update(post, null, false);

        verify(postRepository, times(1)).update(post);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void incrementLikes_shouldIncrementLikes() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0, null);
        postService.incrementLikes(post);
        verify(postRepository, times(1)).incrementLikes(post);
    }

    @Test
    void saveTags_shouldDeleteCurrentTagsAndCreateTagsIfNotExistsAndAttachNewTags() {
        List<String> tags = List.of("tag1");

        when(tagRepository.findTagIdsByNames(tags)).thenReturn(List.of(1L));
        postService.saveTags(1L, tags);

        verify(tagRepository).deleteTagsForPost(1L);
        verify(tagRepository, times(1)).save("tag1");
        verify(tagRepository, times(1)).findTagIdsByNames(tags);
        verify(tagRepository, times(1)).attachTags(1L, List.of(1L));
    }
}
