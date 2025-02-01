package com.blog.repositories;

import com.blog.models.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Tag> findAll() {
        String sql = "SELECT * FROM tags";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Tag.class));
    }

    public Optional<Tag> findByName(String name) {
        String sql = "SELECT * FROM tags WHERE name = ?";
        List<Tag> tags = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Tag.class), name);
        return tags.isEmpty() ? Optional.empty() : Optional.of(tags.get(0));
    }

    public void save(Tag tag) {
        if (tag.getId() == null) {
            String generateIdSql = "CALL NEXT VALUE FOR tag_seq";
            Integer generatedId = jdbcTemplate.queryForObject(generateIdSql, Integer.class);
            tag.setId(generatedId);

            String insertSql = "INSERT INTO tags (id, name) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, tag.getId(), tag.getName());
        } else {
            String updateSql = "UPDATE tags SET name = ? WHERE id = ?";
            jdbcTemplate.update(updateSql, tag.getName(), tag.getId());
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM tags WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
