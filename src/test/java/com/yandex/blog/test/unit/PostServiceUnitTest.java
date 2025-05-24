package com.yandex.blog.test.unit;


import com.yandex.blog.model.Comment;
import com.yandex.blog.model.Post;
import com.yandex.blog.model.Tag;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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
    void findById_shouldCallPostRepositoryFindByIdAndTagRepositoryFindAllTags() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
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
    void findById_shouldCallRepositoryFindById() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        postService.findById(1L);

        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void save_shouldCallSave() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        Post post = new Post(1L, "title1", "desc1", "content1", 0, null);

        postService.save(post, file);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void deleteById_shouldCallPostAndTagRepositoriesDelete() {
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
    void update_shouldCallRepositoryUpdate() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.update(post, null, false);

        verify(postRepository, times(1)).update(post.getId(), post.getTitle(), post.getShortDescription(), post.getContent(), post.getImageUrl());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void incrementLikes_shouldIncrementLikes() {
        Post post = new Post(1L, "title1", "desc1", "content1", 0, null);
        postService.incrementLikes(post);
        verify(postRepository, times(1)).incrementLikes(post.getId());
    }

    @Test
    void saveTags_shouldDeleteCurrentTagsAndCreateTagsIfNotExistsAndAttachNewTags() {
        List<String> tags = List.of("tag1");

        when(tagRepository.findTagIdsByNames(tags)).thenReturn(List.of(1L));
        postService.saveTags(1L, tags);

        verify(tagRepository).deleteTagsForPost(1L);
        verify(tagRepository, times(1)).findTagByName(eq("tag1"));
        verify(tagRepository, times(1)).save(tagWithName("tag1"));
        verify(tagRepository, times(1)).findTagIdsByNames(tags);
        verify(tagRepository, times(1)).attachTag(1L, 1L);
    }

    @Test
    void saveComments_shouldCallRepositorySave() {
        Comment comment = new Comment(1L, 1L, "comment1");

        postService.saveComment(comment);

        verify(commentRepository).save(comment);
    }

    @Test
    void updateComment_shouldCallRepositoryUpdateComment() {
        String updatedContent = "updatedContent";

        postService.updateComment(1L, updatedContent);

        verify(commentRepository).updateContentById(any(Long.class), any(String.class));
    }

    private Tag tagWithName(String tagName) {
        return argThat(tag -> tag.getId() == null && tag.getName().equals(tagName));
    }
}
