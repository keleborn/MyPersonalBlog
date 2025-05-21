package com.yandex.blog.repository;

import com.yandex.blog.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcNativePostRepository implements PostRepository {
    private final static String SELECT_TEXT_FIELD_FROM_POSTS = "select id, title, shortDescription, content, likes, imageUrl from posts p ";
    public static final String LEFT_JOIN_POST_TAG_LINKS = "left join post_tag_links pt on p.id = pt.post_id ";

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findAll() {
        return jdbcTemplate.query(SELECT_TEXT_FIELD_FROM_POSTS,
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("shortDescription"),
                        rs.getString("content"),
                        rs.getInt("likes"),
                        rs.getString("imageUrl")
                )
        );
    }

    @Override
    public List<Post> findAllWithPagination(int limit, int offset, List<Long> tagIds) {
        StringBuilder sql = new StringBuilder(SELECT_TEXT_FIELD_FROM_POSTS);
        if (!tagIds.isEmpty()) {
            sql.append(LEFT_JOIN_POST_TAG_LINKS);
            sql.append(String.format("where pt.tag_id in (%s)", String.join(", ", tagIds.stream()
                    .map(String::valueOf)
                    .toList())));
        }
        sql.append(String.format("limit %d offset %d", limit, offset));

        return jdbcTemplate.query(sql.toString(),
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("shortDescription"),
                        rs.getString("content"),
                        rs.getInt("likes"),
                        rs.getString("imageUrl")
                )
        );
    }

    @Override
    public Post findById(Long id) {
        return jdbcTemplate.queryForObject("select id, title, shortDescription, content, likes, imageUrl from posts where id = ?",
                new Object[]{id},
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("shortDescription"),
                        rs.getString("content"),
                        rs.getInt("likes"),
                        rs.getString("imageUrl")
                )
        );
    }

    @Override
    public int countPosts(List<Long> tagIds) {
        StringBuilder sql = new StringBuilder("select count(*) from posts p ");
        if (!tagIds.isEmpty()) {
            sql.append(LEFT_JOIN_POST_TAG_LINKS);
            sql.append(String.format("where pt.tag_id in (%s)", String.join(", ", tagIds.stream()
                    .map(String::valueOf)
                    .toList())));
        }
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
    }

    @Override
    public void save(Post post) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connections -> {
            PreparedStatement ps = connections.prepareStatement("insert into posts (title, content, shortDescription, imageUrl) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getShortDescription());
            ps.setString(4, post.getImageUrl());
            return ps;
        }, keyHolder);
        post.setId(keyHolder.getKey().longValue());
    }

    @Override
    public void update(Post post) {
        jdbcTemplate.update("update posts set title = ?, shortDescription = ?, content = ?, imageUrl = ? where id = ?",
                post.getTitle(), post.getShortDescription(), post.getContent(), post.getImageUrl(), post.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from posts where id = ?", id);
    }

    @Override
    public void incrementLikes(Post post) {
        jdbcTemplate.update("update posts set likes = likes + 1 where id = ?", post.getId());
    }
}
