package com.blog.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Tag {
    private Integer id;
    private String name;
    private Set<Post> posts;
}
