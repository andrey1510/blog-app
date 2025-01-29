package com.blog.utils;

import com.blog.models.Tag;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TagsUtils {

    public static Set<Tag> getParsedTags(String tags) {
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
