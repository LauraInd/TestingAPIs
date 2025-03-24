package com.svalero.apievents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.controller.EventCategoryController;
import com.svalero.apievents.domain.EventCategory;
import com.svalero.apievents.service.EventCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventCategoryController.class)
public class EventCategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventCategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetAllCategories() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/event-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Music"));
    }

    @Test
    void testAddCategory() throws Exception {
        when(categoryService.saveCategory(any())).thenReturn(category);

        mockMvc.perform(post("/event-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetCategoryById() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/event-categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetCategoriesByName() throws Exception {
        when(categoryService.getCategoriesByName("Music")).thenReturn(List.of(category));

        mockMvc.perform(get("/event-categories/name")
                        .param("name", "Music"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Music"));
    }

    @Test
    void testGetActiveCategories() throws Exception {
        when(categoryService.getActiveCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/event-categories/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void testGetInactiveCategories() throws Exception {
        when(categoryService.getInactiveCategories()).thenReturn(List.of());

        mockMvc.perform(get("/event-categories/inactive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetCategoriesByCreationDate() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(categoryService.getCategoriesByCreationDate(date)).thenReturn(List.of(category));

        mockMvc.perform(get("/event-categories/creation-date")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].creationDate").value("2024-01-01"));
    }

    @Test
    void testGetCategoriesWithMinEvents() throws Exception {
        when(categoryService.getCategoriesWithMinEvents(5)).thenReturn(List.of(category));

        mockMvc.perform(get("/event-categories/min-events")
                        .param("minEvents", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numberEvents").value(5));
    }

    @Test
    void testUpdateCategory() throws Exception {
        when(categoryService.updateCategory(eq(1L), any())).thenReturn(category);

        mockMvc.perform(put("/event-categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/event-categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateCategoryPartial() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("numberEvents", 10);

        when(categoryService.updateEventCategoryPartial(eq(1L), any())).thenReturn(category);

        mockMvc.perform(patch("/event-categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
