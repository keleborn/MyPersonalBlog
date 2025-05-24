package com.yandex.blog.repository;

import com.yandex.blog.model.Comment;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    @Modifying
    @Query("delete from comments where post_id = :postId")
    void deleteAllCommentsByPostId(Long postId);

    @Modifying
    @Query("delete from comments where id = :id")
    void deleteCommentById(Long id);

    @Modifying
    @Query("update comments set content = :content where id = :id")
    void updateContentById(Long id, String content);

    @Query("select count(*) from comments where post_id = :postId")
    int countByPostId(Long postId);
}
