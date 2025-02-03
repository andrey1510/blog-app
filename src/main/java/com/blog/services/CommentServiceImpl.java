package com.blog.services;

import com.blog.dto.CommentDto;
import com.blog.dto.CommentUpdateDto;
import com.blog.exceptions.CommentNotFoundException;
import com.blog.exceptions.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import com.blog.models.Comment;
import com.blog.models.Post;
import org.springframework.stereotype.Service;
import com.blog.repositories.CommentRepository;
import com.blog.repositories.PostRepository;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public Integer getPostIdByCommentId(Integer commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) throw new CommentNotFoundException("Комментарий не найден.");
        return comment.getPost().getId();
    }

    @Override
    public Comment createComment(Integer postId, CommentDto commentDto) {
        Post post = postRepository.findById(postId);
        if (post == null) throw new PostNotFoundException("Пост не найден.");
        Comment comment = Comment.builder()
            .text(commentDto.getText())
            .post(post)
            .build();
        commentRepository.save(comment);
        return comment;
    }

    @Override
    public void updateComment(CommentUpdateDto commentUpdateDto) {
        Comment comment = commentRepository.findById(commentUpdateDto.getId());
        if (comment == null) throw new CommentNotFoundException("Комментарий не найден.");
        comment.setText(commentUpdateDto.getText());
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) throw new CommentNotFoundException("Комментарий не найден.");
        commentRepository.deleteById(commentId);
    }

    @Override
    public void likePost(Integer postId) {
        Post post = postRepository.findById(postId);
        if (post == null) throw new PostNotFoundException("Пост не найден.");
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }
}
