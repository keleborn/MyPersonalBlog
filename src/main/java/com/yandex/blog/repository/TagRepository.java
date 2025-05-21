package com.yandex.blog.repository;

import java.util.List;

public interface TagRepository {
    List<String> findAllTagsByPostId(long postId);

    List<String> findTagByName(String name);

    List<Long> findTagIdsByNames(List<String> names);

    void save(String name);

    void deleteTagsForPost(long postId);

    void attachTags(long postId, List<Long> tagIds);
}
