package com.blog.controllers;

import com.blog.dto.CommentDto;
import com.blog.dto.PostDto;
import lombok.RequiredArgsConstructor;
import com.blog.models.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.blog.services.CommentService;
import com.blog.services.PostService;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    // Создание, редактирование, удаление поста.

    @GetMapping("/{id}")
    public String getPost(@PathVariable UUID id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("newComment", new CommentDto());
        return "post-page";
    }

    @PostMapping
    public String createPost(@ModelAttribute PostDto postDto) {
        postService.createPost(postDto);
        return "redirect:/posts";
    }

    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable UUID id,
                           @ModelAttribute PostDto postDto) {
        postService.updatePost(id, postDto);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    // Лайки; создание, редактирование, удаление комментария.

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable UUID id, @ModelAttribute CommentDto commentDto) {
        commentService.createComment(id, commentDto);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/comments/update/{id}")
    public String editComment(@PathVariable("id") UUID commentId, @ModelAttribute CommentDto commentDto) {
        commentService.updateComment(commentId, commentDto);
        return "redirect:/posts/" + commentService.getPostIdByCommentId(commentId);
    }

    @PostMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable("id") UUID commentId) {
        UUID postId = commentService.getPostIdByCommentId(commentId);
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/like/{id}")
    public String likePost(@PathVariable UUID id) {
        commentService.likePost(id);
        return "redirect:/posts/" + id;
    }

}
