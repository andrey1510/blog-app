package com.blog.repositories;

import com.blog.models.Post;
import com.blog.models.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public Post findById(Integer id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Post.class), id);
    }

    public List<Post> findByTagsName(String tagName, int page, int size) {
        int offset = page * size;
        String sql = """
                SELECT p.* 
                FROM posts p 
                JOIN post_tag pt ON p.id = pt.post_id 
                JOIN tags t ON pt.tag_id = t.id 
                WHERE t.name = ?
                ORDER BY p.id DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Post.class), tagName, size, offset);
    }

    public List<Post> findAll(int page, int size) {
        int offset = page * size;
        String sql = """
                SELECT * 
                FROM posts 
                ORDER BY id DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Post.class), size, offset);
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            String generateIdSql = "CALL NEXT VALUE FOR post_seq";
            Integer generatedId = jdbcTemplate.queryForObject(generateIdSql, Integer.class);
            post.setId(generatedId);
            String insertSql = "INSERT INTO posts (id, title, text, image_path, likes) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(
                insertSql,
                post.getId(),
                post.getTitle(),
                post.getText(),
                post.getImagePath(),
                post.getLikes()
            );
        } else {
            String updateSql = "UPDATE posts SET title = ?, text = ?, image_path = ?, likes = ? WHERE id = ?";
            jdbcTemplate.update(
                updateSql,
                post.getTitle(),
                post.getText(),
                post.getImagePath(),
                post.getLikes(),
                post.getId()
            );
        }
        return post;
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public int getCommentCount(Integer postId) {
        String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, postId);
    }

    public long countAllPosts() {
        String sql = "SELECT COUNT(*) FROM posts";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countPostsByTag(String tagName) {
        String sql = """
                SELECT COUNT(*)
                FROM posts p
                JOIN post_tag pt ON p.id = pt.post_id
                JOIN tags t ON pt.tag_id = t.id
                WHERE t.name = ?
                """;
        return jdbcTemplate.queryForObject(sql, Long.class, tagName);
    }

    public void savePostTags(Integer postId, Set<Tag> tags) {
        for (Tag tag : tags) {
            String sql = "INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, postId, tag.getId());
        }
    }

    public void deletePostTags(Integer postId) {
        String sql = "DELETE FROM post_tag WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }
}