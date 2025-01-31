package com.blog.services;

import com.blog.dto.CommentDto;
import com.blog.dto.CommentUpdateDto;
import com.blog.models.Comment;


public interface CommentService {

    Comment createComment(Integer id, CommentDto commentDto);

    void updateComment(CommentUpdateDto commentUpdateDto);

    void deleteComment(Integer commentId);

    void likePost(Integer postId);

    Integer getPostIdByCommentId(Integer commentId);
}
