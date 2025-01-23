package com.blog.services;

import com.blog.dto.CommentDto;
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
    public Comment createComment(Integer id, CommentDto commentDto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Пост не найден."));
        return commentRepository.save(Comment.builder()
            .text(commentDto.getText())
            .post(post)
            .build());
    }

    @Override
    public Comment updateComment(Integer id, CommentDto commentDto) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден."));;
        comment.setText(commentDto.getText());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден."));
        commentRepository.delete(comment);
    }

    @Override
    public void likePost(Integer postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Пост не найден."));
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }

    @Override
    public Integer getPostIdByCommentId(Integer commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Комментарий не найден.")).getPost().getId();
    }

}
