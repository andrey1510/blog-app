package services;

import dto.CommentDto;
import exceptions.CommentNotFoundException;
import lombok.RequiredArgsConstructor;
import models.Comment;
import org.springframework.stereotype.Service;
import repositories.CommentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;

    @Override
    public Comment getCommentById(UUID id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException("Комментарий не найден."));
    }

    @Override
    public Comment createComment(CommentDto commentDto) {
        Comment comment = Comment.builder()
            .text(commentDto.getText())
            .build();
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(UUID id, CommentDto commentDto) {
        Comment comment = getCommentById(id);
        comment = Comment.builder()
            .id(comment.getId())
            .text(commentDto.getText())
            .build();
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(UUID id) {
        commentRepository.deleteById(id);
    }


}
