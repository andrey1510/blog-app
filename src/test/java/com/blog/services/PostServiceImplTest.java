package com.blog.services;

import com.blog.dto.PostDto;
import com.blog.models.Post;
import com.blog.models.Tag;
import com.blog.repositories.PostRepository;
import com.blog.repositories.TagRepository;
import com.blog.utils.ImageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private PostDto postDto;
    private MockMultipartFile image;

    @BeforeEach
    public void setUp() {
        post = Post.builder()
            .id(1)
            .title("Title 1")
            .text("Text 1")
            .imagePath("image.jpg")
            .likes(0)
            .tags(new HashSet<>())
            .comments(new ArrayList<>())
            .build();

        postDto = new PostDto();
        postDto.setTitle("New Title");
        postDto.setText("New Text");
        postDto.setTags("tag1, tag2, tag3");

        image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test content".getBytes());
    }

    @Test
    public void testGetPostsByTag() {
        Page<Post> page = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findByTagsName(anyString(), any(Pageable.class))).thenReturn(page);

        Page<Post> result = postService.getPostsByTag("tag1", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(postRepository, times(1)).findByTagsName(eq("tag1"), any(Pageable.class));
    }

    @Test
    public void testGetAllPosts() {
        Page<Post> page = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Post> result = postService.getAllPosts(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(postRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testGetPostById() {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(1);

        assertNotNull(result);
        assertEquals("Title 1", result.getTitle());
        verify(postRepository, times(1)).findById(1);
    }

    @Test
    public void testCreatePost() {
        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.createPost(postDto);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void testUpdatePost() throws Exception {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        postDto.setImage(image);

        Post result = postService.updatePost(1, postDto);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void testDeletePost() {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).deleteById(1);
        postService.deletePost(1);

        verify(postRepository, times(1)).findById(1);
        verify(postRepository, times(1)).deleteById(1);
    }

    @Test
    public void testGetPreviewText() {

        String postText = """
            first par string 1
            first par string 2
            first par string 3
            first par string 4
            
            second par string 1
            """;

        Post post = new Post();
        post.setId(4);
        post.setText(postText);

        String preview = postService.getPreviewText(post);

        assertNotNull(preview);
        assertTrue(preview.contains("first par string 3"));
        assertFalse(preview.contains("first par string 4"));
        assertFalse(preview.contains("second par string 1"));
    }

    @Test
    public void testGetCommentCount() {
        int count = postService.getCommentCount(post);

        assertEquals(0, count);
    }
}
