package com.svalero.apievents;

import com.svalero.apievents.domain.EventCategory;
import com.svalero.apievents.repository.EventCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EventCategoryRepositoryTests {

    @Autowired
    private EventCategoryRepository eventCategoryRepository;

    private EventCategory category;

    @BeforeEach
    void setUp() {
        category = new EventCategory();
        category.setName("Music");
        category.setDescription("Music festivals and concerts");
        category.setCreationDate(LocalDate.of(2024, 1, 1));
        category.setNumberEvents(5);
        category.setActive(true);

        eventCategoryRepository.save(category);
    }

    @Test
    void testFindAll() {
        List<EventCategory> categories = eventCategoryRepository.findAll();
        assertFalse(categories.isEmpty());
    }

    @Test
    void testFindByNameContaining() {
        List<EventCategory> results = eventCategoryRepository.findByNameContaining("Music");
        assertEquals(1, results.size());
    }

    @Test
    void testFindByDescriptionContaining() {
        List<EventCategory> results = eventCategoryRepository.findByDescriptionContaining("concerts");
        assertEquals(1, results.size());
    }

    @Test
    void testFindByActiveTrue() {
        List<EventCategory> results = eventCategoryRepository.findByActiveTrue();
        assertEquals(1, results.size());
    }

    @Test
    void testFindByActiveFalse() {
        List<EventCategory> results = eventCategoryRepository.findByActiveFalse();
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindByCreationDate() {
        List<EventCategory> results = eventCategoryRepository.findByCreationDate(LocalDate.of(2024, 1, 1));
        assertEquals(1, results.size());
    }

    @Test
    void testFindByCreationDateAfter() {
        List<EventCategory> results = eventCategoryRepository.findByCreationDateAfter(LocalDate.of(2023, 12, 31));
        assertEquals(1, results.size());
    }

    @Test
    void testFindByCreationDateBefore() {
        List<EventCategory> results = eventCategoryRepository.findByCreationDateBefore(LocalDate.of(2025, 1, 1));
        assertEquals(1, results.size());
    }

    @Test
    void testFindByNumberEvents() {
        List<EventCategory> results = eventCategoryRepository.findByNumberEvents(5);
        assertEquals(1, results.size());
    }

    @Test
    void testFindByNumberEventsGreaterThanEqual() {
        List<EventCategory> results = eventCategoryRepository.findByNumberEventsGreaterThanEqual(3);
        assertEquals(1, results.size());
    }
}