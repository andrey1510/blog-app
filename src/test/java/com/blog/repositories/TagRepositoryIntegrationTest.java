package com.blog.repositories;

import com.blog.configs.TestDatasourceConfig;
import com.blog.configs.TestWebConfiguration;
import com.blog.models.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TagRepositoryIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    private Tag tag1;

    @BeforeEach
    void setUp() {
        tag1 = Tag.builder()
            .name("Java")
            .build();
        tagRepository.save(tag1);

        Tag tag2 = Tag.builder()
            .name("Spring")
            .build();
        tagRepository.save(tag2);
    }

    @AfterEach
    void tearDown(){
        tagRepository.deleteAll();
    }


    @Test
    void testFindByName() {
        Optional<Tag> foundTag = tagRepository.findByName("Java");
        assertTrue(foundTag.isPresent());
        assertEquals(tag1, foundTag.get());
    }

    @Test
    void testFindAll() {
        List<Tag> tags = tagRepository.findAll();
        assertFalse(tags.isEmpty());
    }

    @Test
    void testSave() {
        Tag newTag = Tag.builder()
            .name("Hibernate")
            .build();
        Tag savedTag = tagRepository.save(newTag);
        assertNotNull(savedTag.getId());
    }

    @Test
    void testDeleteById() {
        tagRepository.deleteById(tag1.getId());
        Optional<Tag> deletedTag = tagRepository.findById(tag1.getId());
        assertFalse(deletedTag.isPresent());
    }
}
