package controllers;

import dto.CommentDto;
import dto.PostDto;
import lombok.RequiredArgsConstructor;
import models.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import services.PostService;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

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

}
