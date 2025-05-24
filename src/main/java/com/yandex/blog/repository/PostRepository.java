package com.yandex.blog.repository;

import com.yandex.blog.model.Post;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findAll();

    @Query("""
            select p.id, p.title, p.content, p.likes, p.image_url, p.short_description from posts p
            order by p.id
            limit :limit offset :offset
           """)
    List<Post> findAllWithPagination(int limit, int offset);

    @Query("""
            select p.id, p.title, p.content, p.likes, p.image_url, p.short_description from posts p join post_tag_links pt on p.id = pt.post_id 
            where pt.tag_id in (:tagIds)
            order by p.id
            limit :limit offset :offset
           """)
    List<Post> findAllWithPagination(int limit, int offset, List<Long> tagIds);

    @Query("select count(*) from posts p join post_tag_links pt on p.id = pt.post_id where pt.tag_id in (:tagIds)")
    int countPosts(List<Long> tagIds);

    @Query("select count(*) from posts")
    int countPosts();

    @Modifying
    @Query("update posts set title = :title, short_description = :shortDescription, content = :content, image_url = :imageUrl where id = :id")
    void update(Long id, String title, String shortDescription, String content, String imageUrl);

    @Modifying
    @Query("update posts set likes = likes + 1 where id = :id")
    void incrementLikes(Long id);
}
