package com.blog.services;

import com.blog.dto.CommentDto;
import com.blog.models.Comment;


public interface CommentService {


    Comment createComment(Integer id, CommentDto commentDto);

    Comment updateComment(Integer id, CommentDto commentDto);

    void deleteComment(Integer commentId);

    void likePost(Integer postId);

    Integer getPostIdByCommentId(Integer commentId);
}
