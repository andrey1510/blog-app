package com.blog.repositories;

import com.blog.models.Comment;
import com.blog.models.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PostRepository postRepository;

    public Comment findById(Integer id) {
        String commentSql = """
                SELECT c.id, c.text, c.post_id 
                FROM comments c 
                WHERE c.id = ?
                """;
        Comment comment = jdbcTemplate.queryForObject(commentSql, (rs, rowNum) -> {
            Comment c = new Comment();
            c.setId(rs.getInt("id"));
            c.setText(rs.getString("text"));

            Integer postId = rs.getInt("post_id");
            Post post = postRepository.findById(postId);

            c.setPost(post);
            return c;
        }, id);

        return comment;
    }

    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            String generateIdSql = "CALL NEXT VALUE FOR comment_seq";
            Integer generatedId = jdbcTemplate.queryForObject(generateIdSql, Integer.class);
            comment.setId(generatedId);

            String insertSql = "INSERT INTO comments (id, text, post_id) VALUES (?, ?, ?)";
            jdbcTemplate.update(
                insertSql,
                comment.getId(),
                comment.getText(),
                comment.getPost().getId()
            );
        } else {
            String updateSql = "UPDATE comments SET text = ? WHERE id = ?";
            jdbcTemplate.update(updateSql, comment.getText(), comment.getId());
        }
        return comment;
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}