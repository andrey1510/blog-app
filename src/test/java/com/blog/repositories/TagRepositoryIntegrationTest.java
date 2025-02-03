package com.blog.repositories;

import com.blog.configs.TestDatasourceConfig;
import com.blog.configs.TestWebConfiguration;
import com.blog.models.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestWebConfiguration.class, TestDatasourceConfig.class})
@Transactional
@Rollback
public class TagRepositoryIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        Tag tag1 = Tag.builder()
            .name("tag1")
            .build();
        tagRepository.save(tag1);

        Tag tag2 = Tag.builder()
            .name("tag2")
            .build();
        tagRepository.save(tag2);
    }

    @Test
    void testFindByName() {
        Optional<Tag> tag = tagRepository.findByName("tag1");
        assertTrue(tag.isPresent());
        assertEquals("tag1", tag.get().getName());
    }

    @Test
    void testFindAll() {
        List<Tag> tags = tagRepository.findAll();
        assertFalse(tags.isEmpty());
        assertEquals(2, tags.size());
        assertTrue(tags.stream().anyMatch(tag -> "tag1".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "tag2".equals(tag.getName())));
    }

    @Test
    void testSave() {
        Tag newTag = Tag.builder()
            .name("newTag")
            .build();
        tagRepository.save(newTag);

        List<Tag> tags = tagRepository.findAll();
        assertNotNull(tags);
        assertEquals(3, tags.size());
        assertTrue(tags.stream().anyMatch(tag -> "newTag".equals(tag.getName())));
    }
}
