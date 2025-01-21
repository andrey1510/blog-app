package com.blog.services;

import com.blog.dto.CommentDto;
import com.blog.models.Comment;

import java.util.UUID;

public interface CommentService {


    Comment createComment(UUID id, CommentDto commentDto);

    Comment updateComment(UUID id, CommentDto commentDto);

    void deleteComment(UUID commentId);

    void likePost(UUID postId);

    UUID getPostIdByCommentId(UUID commentId);
}
