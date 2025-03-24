package com.svalero.apievents;

import com.svalero.apievents.domain.EventCategory;
import com.svalero.apievents.exception.EventCategoryNotFoundException;
import com.svalero.apievents.repository.EventCategoryRepository;
import com.svalero.apievents.service.EventCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventCategoryServiceTests {

    @Mock
    private EventCategoryRepository categoryRepository;

    @InjectMocks
    private EventCategoryService categoryService;

    private EventCategory category;

    @BeforeEach
    void setUp() {
        category = new EventCategory();
        category.setId(1L);
        category.setName("Music");
        category.setDescription("Concerts and festivals");
        category.setCreationDate(LocalDate.of(2024, 1, 1));
        category.setNumberEvents(5);
        category.setActive(true);
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        List<EventCategory> result = categoryService.getAllCategories();
        assertEquals(1, result.size());
    }

    @Test
    void testGetCategoryByIdFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        EventCategory result = categoryService.getCategoryById(1L);
        assertNotNull(result);
        assertEquals("Music", result.getName());
    }

    @Test
    void testGetCategoryByIdNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EventCategoryNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }

    @Test
    void testSaveCategory() {
        when(categoryRepository.save(any())).thenReturn(category);
        EventCategory saved = categoryService.saveCategory(category);
        assertEquals("Music", saved.getName());
    }

    @Test
    void testUpdateCategory() {
        EventCategory updated = new EventCategory();
        updated.setName("Updated");
        updated.setDescription("Updated Desc");
        updated.setCreationDate(LocalDate.of(2023, 5, 1));
        updated.setNumberEvents(10);
        updated.setActive(false);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any())).thenReturn(updated);

        EventCategory result = categoryService.updateCategory(1L, updated);
        assertEquals("Updated", result.getName());
        assertEquals(10, result.getNumberEvents());
    }

    @Test
    void testUpdatePartialCategory() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("numberEvents", 15);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any())).thenReturn(category);

        EventCategory result = categoryService.updateEventCategoryPartial(1L, updates);
        assertEquals(15, result.getNumberEvents());
    }

    @Test
    void testDeleteCategorySuccess() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);
        categoryService.deleteCategory(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(categoryRepository.existsById(2L)).thenReturn(false);
        assertThrows(EventCategoryNotFoundException.class, () -> categoryService.deleteCategory(2L));
    }

    @Test
    void testGetCategoriesByName() {
        when(categoryRepository.findByNameContaining("Music")).thenReturn(List.of(category));
        assertEquals(1, categoryService.getCategoriesByName("Music").size());
    }

    @Test
    void testGetActiveCategories() {
        when(categoryRepository.findByActiveTrue()).thenReturn(List.of(category));
        assertEquals(1, categoryService.getActiveCategories().size());
    }

    @Test
    void testGetCategoriesByNumberOfEvents() {
        when(categoryRepository.findByNumberEvents(5)).thenReturn(List.of(category));
        assertEquals(1, categoryService.getCategoriesByNumberOfEvents(5).size());
    }
}
