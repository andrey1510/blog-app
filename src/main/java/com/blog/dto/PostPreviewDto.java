package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

}
