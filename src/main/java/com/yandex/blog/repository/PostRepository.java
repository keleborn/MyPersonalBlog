package com.yandex.blog.repository;

import com.yandex.blog.model.Post;

import java.util.List;

public interface PostRepository {
    List<Post> findAll();
    List<Post> findAllWithPagination(int limit, int offset, List<Long> tagIds);
    Post findById(Long id);
    int countPosts(List<Long> tags);
    void deleteById(Long id);
    void save(Post post);
    void update(Post post);
    void incrementLikes(Post post);
}
