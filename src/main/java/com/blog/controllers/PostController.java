package com.blog.controllers;

import com.blog.dto.CommentDto;
import com.blog.dto.PostDto;
import com.blog.services.TagService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.blog.services.CommentService;
import com.blog.services.PostService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;

    // Лента постов, переход к посту и созданию поста
    //ToDo post preview, delete/update controls

    @GetMapping
    public String getPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           Model model) {
        Page<Post> posts = postService.getAllPosts(page, size);
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("pageSize", size);
        return "posts";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable("id") Integer id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("newComment", new CommentDto());
        return "post";
    }

    // Создание, редактирование, удаление поста.

    @PostMapping
    public String createPost(@ModelAttribute PostDto postDto,
                             @RequestParam(value = "image", required = false) MultipartFile image) {
        postDto.setImage(image);
        postService.createPost(postDto);
        return "redirect:/posts";
    }

    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable("id") Integer id,
                             @ModelAttribute PostDto postDto,
                             @RequestParam(value = "image", required = false) MultipartFile image) {
        postDto.setImage(image);
        postService.updatePost(id, postDto);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Integer id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    // Лайки; создание, редактирование, удаление комментария.

    @PostMapping("/{id}/comments")
    public String createComment(@PathVariable("id") Integer id, @ModelAttribute CommentDto commentDto) {
        commentService.createComment(id, commentDto);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/comments/update/{id}")
    public ResponseEntity<?> updateComment(@PathVariable("id") Integer commentId, @RequestBody CommentDto commentDto) {
        commentService.updateComment(commentId, commentDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable("id") Integer commentId) {
        Integer postId = commentService.getPostIdByCommentId(commentId);
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/like/{id}")
    public String likePost(@PathVariable("id") Integer id) {
        commentService.likePost(id);
        return "redirect:/posts/" + id;
    }

}
