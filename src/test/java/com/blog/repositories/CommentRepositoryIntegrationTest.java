package com.blog.repositories;

import com.blog.configs.TestDatasourceConfig;
import com.blog.configs.TestWebConfiguration;
import com.blog.models.Comment;
import com.blog.models.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestWebConfiguration.class, TestDatasourceConfig.class})
@Transactional
public class CommentRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Post post1;
    private Comment comment1;

    @BeforeEach
    void setUp() {
        post1 = Post.builder()
            .title("Title 1")
            .text("Text 1")
            .tags(Collections.emptySet())
            .likes(10)
            .build();
        postRepository.save(post1);

        comment1 = Comment.builder()
            .text("Comment text 1")
            .post(post1)
            .build();
        commentRepository.save(comment1);
    }

    @AfterEach
    void tearDown(){
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void testSave() {
        Comment newComment = Comment.builder()
            .text("New Comment text")
            .post(post1)
            .build();
        Comment savedComment = commentRepository.save(newComment);
        assertNotNull(savedComment.getId());
    }

    @Test
    void testFindById() {
        Optional<Comment> foundComment = commentRepository.findById(comment1.getId());
        assertTrue(foundComment.isPresent());
        assertEquals(comment1, foundComment.get());
    }

    @Test
    void testDeleteById() {
        commentRepository.deleteById(comment1.getId());
        Optional<Comment> deletedComment = commentRepository.findById(comment1.getId());
        assertFalse(deletedComment.isPresent());
    }
}
