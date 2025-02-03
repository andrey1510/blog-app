package com.blog.repositories;

import com.blog.configs.TestDatasourceConfig;
import com.blog.configs.TestWebConfiguration;
import com.blog.models.Post;
import com.blog.models.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestWebConfiguration.class, TestDatasourceConfig.class})
@Transactional
@Rollback
public class PostRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Post post1;

    @BeforeEach
    void setUp() {

        post1 = Post.builder()
            .title("Title 1")
            .text("Text 1")
            .tags(Collections.emptySet())
            .likes(10)
            .build();
        postRepository.save(post1);

        Post post2 = Post.builder()
            .title("Title 2")
            .text("Text 2")
            .tags(Collections.emptySet())
            .likes(20)
            .build();
        postRepository.save(post2);

        Tag tag2 = Tag.builder()
            .id(1)
            .name("tag2")
            .posts(Collections.singleton(post1))
            .build();
        tagRepository.save(tag2);
    }

    @Test
    void testFindById() {
        Post foundPost = postRepository.findById(post1.getId());
        assertNotNull(foundPost);
        assertEquals(post1.getTitle(), foundPost.getTitle());
    }

    @Test
    void testFindAll() {
        List<Post> posts = postRepository.findAll(0, 10);
        assertNotNull(posts);
        assertEquals(2, posts.size());
        assertEquals("Title 2", posts.get(0).getTitle());
    }

    @Test
    void testSave() {
        Post newPost = Post.builder()
            .title("New Title")
            .text("New Text")
            .likes(5)
            .build();
        postRepository.save(newPost);

        Post savedPost = postRepository.findById(newPost.getId());

        assertNotNull(savedPost.getId());
        assertEquals(newPost.getTitle(), savedPost.getTitle());
    }

    @Test
    void testDeleteById() {
        postRepository.deleteById(post1.getId());

        assertThrows(EmptyResultDataAccessException.class, () -> {
            postRepository.findById(post1.getId());
        });
    }

    @Test
    void testGetCommentCount() {
        String commentSql = "INSERT INTO comments (id, text, post_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(commentSql, 1, "Comment 1", post1.getId());
        jdbcTemplate.update(commentSql, 2, "Comment 2", post1.getId());

        int commentCount = postRepository.getCommentCount(post1.getId());
        assertEquals(2, commentCount);
    }

    @Test
    void testFindByTagsName() {
        List<Post> posts = postRepository.findByTagsName("tag1", 0, 10);

        assertNotNull(posts);
    }

    @Test
    void testCountAllPosts() {
        long count = postRepository.countAllPosts();

        assertEquals(2, count);
    }
}