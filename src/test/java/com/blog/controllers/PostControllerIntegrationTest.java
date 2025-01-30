package com.blog.controllers;

import com.blog.dto.CommentDto;
import com.blog.dto.PostDto;
import com.blog.exceptions.PostNotFoundException;
import com.blog.models.Comment;
import com.blog.models.Post;
import com.blog.models.Tag;
import com.blog.repositories.CommentRepository;
import com.blog.repositories.PostRepository;
import com.blog.repositories.TagRepository;
import com.blog.services.PostService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentRepository commentRepository;

    private PostDto postDto;
    private MockMultipartFile image;

    @BeforeEach
    void setUp() {
        Post post1 = Post.builder()
            .id(1)
            .title("Title 1")
            .text("Text 1")
            .imagePath("/path/to/image")
            .tags(Collections.emptySet())
            .likes(1)
            .build();
        Post post2 = Post.builder()
            .id(2)
            .title("Title 2")
            .text("Text 2")
            .imagePath("/path/to/image")
            .tags(Collections.emptySet())
            .likes(1)
            .comments(Collections.emptyList())
            .build();
        postRepository.save(post1);
        postRepository.save(post2);

        Comment comment = Comment.builder()
            .id(1)
            .text("Comment")
            .post(post1)
            .build();
        commentRepository.save(comment);

        Tag tag = Tag.builder()
            .id(1)
            .name("Tag")
            .posts(Collections.singleton(post1))
            .build();
        tagRepository.save(tag);

        postDto = new PostDto("New Title", "New Text", null, "New Tag");
        image = new MockMultipartFile("image", "filename.png", "image/png", "some-image".getBytes());
    }

    @Test
    @SneakyThrows
    void testLikePost() {
        mockMvc.perform(multipart("/posts/like/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/posts/1"));

        Post likedPost = postService.getPostById(1);
        assertEquals(2, likedPost.getLikes());
    }

    @Test
    @SneakyThrows
    void testGetPost() {
        mockMvc.perform(get("/posts/1"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("post"));
    }

    @Test
    @SneakyThrows
    void testGetPosts() {
        mockMvc.perform(get("/posts")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("posts"))
            .andExpect(model().attributeExists("currentPage"))
            .andExpect(model().attributeExists("totalPages"))
            .andExpect(model().attributeExists("pageSize"))
            .andExpect(model().attributeExists("tags"));
    }

    @Test
    @SneakyThrows
    void testCreatePost() {
        mockMvc.perform(multipart("/posts")
                .file(image)
                .param("title", postDto.getTitle())
                .param("text", postDto.getText()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/posts"));
    }

    @Test
    @SneakyThrows
    void testUpdatePost() {
        postDto.setImage(image);
        mockMvc.perform(multipart("/posts/update/1")
                .file(image)
                .param("title", postDto.getTitle())
                .param("text", postDto.getText()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/posts/1"));
        Post updatedPost = postService.getPostById(1);

        assertEquals("New Title", updatedPost.getTitle());
        assertEquals("New Text", updatedPost.getText());
    }

    @Test
    @SneakyThrows
    void testDeletePost() {
        assertNotNull(postService.getPostById(2), "Поста не существует в базе.");

        mockMvc.perform(post("/posts/delete/2"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/posts"));

        assertThrows(PostNotFoundException.class, () -> {
            postService.getPostById(2);
        });
    }

    @Test
    @SneakyThrows
    void testCreateComment() {
        mockMvc.perform(post("/posts/1/comments")
                .flashAttr("commentDto", new CommentDto("Comment")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/posts/1"));

        List<Comment> comments = postService.getPostById(1).getComments();
        assertFalse(comments.isEmpty());
        assertEquals("Comment", comments.get(1).getText());
    }

    @Test
    @SneakyThrows
    void testDeleteComment() {
        mockMvc.perform(post("/posts/comments/delete/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/posts/1"));
        assertFalse(commentRepository.findById(1).isPresent());
    }

}
