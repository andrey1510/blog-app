package com.blog.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Post {
    private Integer id;
    private String title;
    private String text;
    private String imagePath;
    private Set<Tag> tags;
    private int likes;
    // @JsonIgnore
    private List<Comment> comments;
}