package com.blog.controllers;

import com.blog.dto.CommentDto;
import com.blog.dto.CommentUpdateDto;
import com.blog.dto.PostDto;
import com.blog.dto.PostPreviewDto;
import com.blog.exceptions.CommentNotFoundException;
import com.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import com.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;

    // Методы для получения постов

    @GetMapping
    public String getPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           @RequestParam(value = "tag", required = false) String tag,
                           Model model) {
        Page<Post> postsPage;

        if (tag != null && !tag.isEmpty()) {
            postsPage = postService.getPostsByTag(tag, page, size);
            model.addAttribute("selectedTag", tag);
        } else {
            postsPage = postService.getAllPosts(page, size);
            model.addAttribute("selectedTag", null);
        }

        List<Post> posts = postsPage.getContent();
        List<PostPreviewDto> postPreviews = new ArrayList<>();

        for (Post post : posts) {
            PostPreviewDto previewDto = new PostPreviewDto(
                post.getId(),
                post.getTitle(),
                post.getImagePath(),
                postService.getPreviewText(post),
                postService.getCommentCount(post),
                post.getLikes()
            );
            postPreviews.add(previewDto);
        }

        model.addAttribute("posts", postPreviews);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postsPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("tags", tagService.getAllTags());

        return "posts";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable("id") Integer id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("newComment", new CommentDto());
        return "post";
    }

    // Методы для создания, редактирования, удаления поста.

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

    @PostMapping("/return-to-posts")
    public String returnToPosts() {
        return "redirect:/posts";
    }

    // Методы для создания, редактирования, удаления комментария

    @PostMapping("/{id}/comments")
    public String createComment(@PathVariable("id") Integer id, @ModelAttribute CommentDto commentDto) {
        commentService.createComment(id, commentDto);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/comments/update")
    public ResponseEntity<?> updateComment(@RequestBody CommentUpdateDto commentUpdateDto) {
        try {
            commentService.updateComment(commentUpdateDto);
            return ResponseEntity.ok().build();
        } catch (CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable("id") Integer commentId) {
        Integer postId = commentService.getPostIdByCommentId(commentId);
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }

    // Проставление лайка

    @PostMapping("/like/{id}")
    public String likePost(@PathVariable("id") Integer id) {
        commentService.likePost(id);
        return "redirect:/posts/" + id;
    }

}
