package com.blog.dto;

import com.blog.models.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostPreviewDto {

    private Integer id;

    private String title;

    private String imagePath;

    private String previewText;

    private int commentCount;

    private int likes;

    private Set<Tag> tags;

}
