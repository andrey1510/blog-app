package com.blog.services;

import com.blog.dto.CommentDto;
import com.blog.dto.CommentUpdateDto;
import com.blog.models.Comment;
import com.blog.models.Post;
import com.blog.repositories.CommentRepository;
import com.blog.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Post post;
    private Comment comment;
    private CommentDto commentDto;
    private CommentUpdateDto commentUpdateDto;

    @BeforeEach
    public void setUp() {
        post = new Post();
        post.setId(1);
        post.setComments(new ArrayList<>());

        comment = new Comment();
        comment.setId(1);
        comment.setText("Text 1");
        comment.setPost(post);

        commentDto = new CommentDto();
        commentDto.setText("Create Text");

        commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setId(1);
        commentUpdateDto.setText("New Text");
    }

    @Test
    public void testCreateComment() {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.createComment(1, commentDto);

        assertNotNull(result);
        assertEquals("Create Text", result.getText());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testUpdateComment() {
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

        commentService.updateComment(commentUpdateDto);

        assertEquals("New Text", comment.getText());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    public void testDeleteComment() {
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        commentService.deleteComment(1);

        verify(postRepository, times(1)).save(post);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    public void testLikePost() {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        commentService.likePost(1);

        assertEquals(1, post.getLikes());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void testGetPostIdByCommentId() {
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

        Integer postId = commentService.getPostIdByCommentId(1);

        assertEquals(1, postId);
        verify(commentRepository, times(1)).findById(1);
    }

}
