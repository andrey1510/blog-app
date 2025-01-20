package services;

import dto.CommentDto;
import exceptions.CommentNotFoundException;
import exceptions.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import models.Comment;
import models.Post;
import org.springframework.stereotype.Service;
import repositories.CommentRepository;
import repositories.PostRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public Comment createComment(UUID id, CommentDto commentDto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Пост не найден."));
        return commentRepository.save(Comment.builder()
            .text(commentDto.getText())
            .post(post)
            .build());
    }

    @Override
    public Comment updateComment(UUID id, CommentDto commentDto) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден."));;
        comment.setText(commentDto.getText());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден."));
        commentRepository.delete(comment);
    }

    @Override
    public void likePost(UUID postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Пост не найден."));
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }

    @Override
    public UUID getPostIdByCommentId(UUID commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Комментарий не найден.")).getPost().getId();
    }

}
