package com.blog.controllers;

import com.blog.dto.CommentDto;
import com.blog.dto.PostDto;
import com.blog.models.Tag;
import com.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import com.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.blog.services.CommentService;
import com.blog.services.PostService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;

    // Лента постов, переход к посту

    @GetMapping
    public String getPosts(@RequestParam(required = false) String tag,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        Page<Post> posts = postService.getPosts(tag, page, size);
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("posts", posts);
        model.addAttribute("tags", tags);
        model.addAttribute("currentTag", tag);
        return "posts";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable UUID id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("newComment", new CommentDto());
        return "post";
    }

    // Создание, редактирование, удаление поста.

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
