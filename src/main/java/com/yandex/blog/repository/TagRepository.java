package com.yandex.blog.repository;

import com.yandex.blog.model.Tag;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {
    List<Tag> findAll();
    @Query("select name from tags t join post_tag_links pt on t.id = pt.tag_id where pt.post_id = :postId")
    List<String> findAllTagsByPostId(long postId);

    @Query("select name from tags where name = :name")
    String findTagByName(String name);

    @Query("select id from tags where name in (:names)")
    List<Long> findTagIdsByNames(List<String> names);

    @Modifying
    @Query("delete from post_tag_links where post_id = :postId")
    void deleteTagsForPost(long postId);

    @Modifying
    @Query("insert into post_tag_links (post_id, tag_id) values (:postId, :tagId)")
    void attachTag(long postId, long tagId);

    @Query("select post_id from post_tag_links")
    List<Long> showAllTagsPostIds();

    @Query("select tag_id from post_tag_links")
    List<Long> showAllTagsTagIds();
}
