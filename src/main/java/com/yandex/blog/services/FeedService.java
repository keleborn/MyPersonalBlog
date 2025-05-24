package com.yandex.blog.services;

import com.yandex.blog.model.Post;
import com.yandex.blog.repository.CommentRepository;
import com.yandex.blog.repository.PostRepository;
import com.yandex.blog.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    public FeedService(PostRepository postRepository, TagRepository tagRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    public List<Post> findAll() {
        List<Post> posts = postRepository.findAll();
        posts.forEach(this::setTags);
        return posts;
    }

    public List<Post> findAllByTagsWithPagination(int limit, int page, List<String> tags) {
        int offset = (page - 1) * limit;
        List<Post> posts = new ArrayList<>();
        if (tags == null || tags.isEmpty()) {
            posts = postRepository.findAllWithPagination(limit, offset);
        } else {
            posts = postRepository.findAllWithPagination(limit, offset, tagRepository.findTagIdsByNames(tags));
        }
        posts.forEach(this::setTags);
        return posts;
    }

    public int countPosts(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            return postRepository.countPosts();
        }
        return postRepository.countPosts(tagRepository.findTagIdsByNames(tagList));
    }

    private void setTags(Post post) {
        post.setTags(tagRepository.findAllTagsByPostId(post.getId()));
    }
}
