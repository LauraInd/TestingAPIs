package com.svalero.apievents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.controller.EventCategoryController;
import com.svalero.apievents.domain.EventCategory;
import com.svalero.apievents.exception.EventCategoryNotFoundException;
import com.svalero.apievents.service.EventCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventCategoryController.class)
public class EventCategoryControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventCategoryService categoryService;

    @Test
    void testGetAllCategories_ReturnsOk() throws Exception {
        List<EventCategory> categories = List.of(
                new EventCategory(1L, "Music", "Live shows", LocalDate.now(), 10, true, null),
                new EventCategory(2L, "Tech", "Conferences", LocalDate.now(), 5, true, null)
        );

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/event-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void testAddCategory_ReturnsCreated() throws Exception {
        EventCategory category = new EventCategory(null, "Art", "Gallery shows", LocalDate.now(), 3, true, null);
        EventCategory saved = new EventCategory(1L, "Art", "Gallery shows", LocalDate.now(), 3, true, null);

        when(categoryService.saveCategory(any(EventCategory.class))).thenReturn(saved);

        mockMvc.perform(post("/event-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Art"));
    }

    @Test
    void testGetCategoryById_ReturnsOk() throws Exception {
        EventCategory category = new EventCategory(1L, "Sport", "Outdoor events", LocalDate.now(), 7, true, null);
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/event-categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sport"));
    }

    @Test
    void testGetCategoryById_NotFound() throws Exception {
        when(categoryService.getCategoryById(99L)).thenThrow(new EventCategoryNotFoundException("Category not found"));

        mockMvc.perform(get("/event-categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }

    @Test
    void testUpdateCategory_ReturnsOk() throws Exception {
        EventCategory updated = new EventCategory(1L, "Updated", "Updated desc", LocalDate.now(), 10, false, null);
        when(categoryService.updateCategory(eq(1L), any(EventCategory.class))).thenReturn(updated);

        mockMvc.perform(put("/event-categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void testUpdateCategoryPartial_ReturnsOk() throws Exception {
        Map<String, Object> updates = Map.of("active", false);
        EventCategory updated = new EventCategory(1L, "Music", "Live shows", LocalDate.now(), 10, false, null);

        when(categoryService.updateEventCategoryPartial(eq(1L), anyMap())).thenReturn(updated);

        mockMvc.perform(patch("/event-categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void testDeleteCategory_ReturnsNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/event-categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    void testDeleteCategory_NotFound() throws Exception {
        doThrow(new EventCategoryNotFoundException("Category not found")).when(categoryService).deleteCategory(99L);

        mockMvc.perform(delete("/event-categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }
}
