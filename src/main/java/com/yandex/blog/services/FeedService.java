package com.yandex.blog.services;

import com.yandex.blog.model.Post;
import com.yandex.blog.repository.PostRepository;
import com.yandex.blog.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yandex.blog.utils.PostUtils.parseTags;

@Service
public class FeedService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public FeedService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public List<Post> findAllByTagsWithPagination(int limit, int page, List<String> tags) {
        int offset = (page - 1) * limit;
        List<Post> posts = postRepository.findAllWithPagination(limit, offset, tagRepository.findTagIdsByNames(tags));
        posts.forEach(this::setTags);
        return posts;
    }

    public int countPosts(List<String> tagList) {
        return postRepository.countPosts(tagRepository.findTagIdsByNames(tagList));
    }

    private void setTags(Post post) {
        post.setTags(tagRepository.findAllTagsByPostId(post.getId()));
    }
}
