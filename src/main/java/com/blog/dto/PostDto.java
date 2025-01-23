package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import com.blog.models.Tag;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostDto {

    private String title;

    private String text;

    private String tags;

    public Set<Tag> getParsedTags() {
        if (tags == null || tags.isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(tags.split(","))
            .map(String::trim)
            .filter(tag -> !tag.isEmpty())
            .map(tag -> new Tag(null, tag, null))
            .collect(Collectors.toSet());
    }
}