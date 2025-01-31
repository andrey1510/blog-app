package com.blog.repositories;

import com.blog.configs.TestDatasourceConfig;
import com.blog.configs.TestWebConfiguration;
import com.blog.models.Post;
import com.blog.models.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestWebConfiguration.class, TestDatasourceConfig.class})
@Transactional
public class PostRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

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

        Tag tag1 = Tag.builder()
            .id(1)
            .name("tag1")
            .posts(Collections.singleton(post1))
            .build();
        tagRepository.save(tag1);
    }

    @AfterEach
    void tearDown(){
        tagRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<Post> foundPost = postRepository.findById(post1.getId());
        assertTrue(foundPost.isPresent());
        assertEquals(post1, foundPost.get());
    }

    @Test
    void testFindAll() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Post> posts = postRepository.findAll(pageRequest);
        assertFalse(posts.getContent().isEmpty());
    }

    @Test
    void testSave() {
        Post newPost = Post.builder()
            .title("New Title")
            .text("New Text")
            .likes(5)
            .build();

        Post savedPost = postRepository.save(newPost);
        assertNotNull(savedPost.getId());
    }

    @Test
    void testDeleteById() {
        postRepository.deleteById(post1.getId());
        Optional<Post> deletedPost = postRepository.findById(post1.getId());
        assertFalse(deletedPost.isPresent());
    }
}
