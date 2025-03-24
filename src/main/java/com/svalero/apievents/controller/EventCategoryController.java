package com.svalero.apievents.controller;

import com.svalero.apievents.domain.EventCategory;
import com.svalero.apievents.exception.EventCategoryNotFoundException;
import com.svalero.apievents.service.EventCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/event-categories")
public class EventCategoryController {

    private final Logger logger = LoggerFactory.getLogger(EventCategoryController.class);

    private final EventCategoryService categoryService;

    @Autowired
    public EventCategoryController(EventCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Obtener todas las categorías
    @GetMapping
    public ResponseEntity<List<EventCategory>> getAllCategories() {
        logger.info("BEGIN getAllCategories");
        List<EventCategory> categories = categoryService.getAllCategories();
        logger.info("END getAllCategories - Total categories fetched: {}", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Agregar una nueva categoría
    @PostMapping
    public ResponseEntity<EventCategory> addCategory(@RequestBody EventCategory category) {
        logger.info("BEGIN addCategory - Adding new category: {}", category.getName());
        EventCategory newCategory = categoryService.saveCategory(category);
        logger.info("END addCategory - Category added with ID: {}", newCategory.getId());
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    // Buscar categorías por nombre
    @GetMapping("/name")
    public ResponseEntity<List<EventCategory>> getCategoriesByName(@RequestParam String name) {
        logger.info("BEGIN getCategoriesByName - Searching categories with name: {}", name);
        List<EventCategory> categories = categoryService.getCategoriesByName(name);
        logger.info("END getCategoriesByName - Total categories found: {}", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Buscar categorías activas
    @GetMapping("/active")
    public ResponseEntity<List<EventCategory>> getActiveCategories() {
        logger.info("BEGIN getActiveCategories - Fetching active categories");
        List<EventCategory> categories = categoryService.getActiveCategories();
        logger.info("END getActiveCategories - Total active categories found: {}", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Buscar categorías inactivas
    @GetMapping("/inactive")
    public ResponseEntity<List<EventCategory>> getInactiveCategories() {
        logger.info("BEGIN getInactiveCategories - Fetching inactive categories");
        List<EventCategory> categories = categoryService.getInactiveCategories();
        logger.info("END getInactiveCategories - Total inactive categories found: {}", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Buscar categorías por fecha de creación
    @GetMapping("/creation-date")
    public ResponseEntity<List<EventCategory>> getCategoriesByCreationDate(@RequestParam LocalDate date) {
        logger.info("BEGIN getCategoriesByCreationDate - Searching categories created on: {}", date);
        List<EventCategory> categories = categoryService.getCategoriesByCreationDate(date);
        logger.info("END getCategoriesByCreationDate - Total categories found: {}", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Buscar categorías con al menos un número mínimo de eventos
    @GetMapping("/min-events")
    public ResponseEntity<List<EventCategory>> getCategoriesWithMinEvents(@RequestParam int minEvents) {
        logger.info("BEGIN getCategoriesWithMinEvents - Searching categories with at least {} events", minEvents);
        List<EventCategory> categories = categoryService.getCategoriesWithMinEvents(minEvents);
        logger.info("END getCategoriesWithMinEvents - Total categories found: {}", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<EventCategory> getCategoryById(@PathVariable Long id) {
        logger.info("BEGIN getCategoryById - Fetching category with ID: {}", id);
        try {
            EventCategory category = categoryService.getCategoryById(id);
            logger.info("END getCategoryById - Category found: {}", category.getId());
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getCategoryById - Category not found with ID: {}", id, e);
            throw e;
        }
    }

    // Actualizar una categoría por ID
    @PutMapping("/{id}")
    public ResponseEntity<EventCategory> updateCategory(@PathVariable Long id, @RequestBody EventCategory categoryDetails) {
        logger.info("BEGIN updateCategory - Updating category with ID: {}", id);
        try {
            EventCategory updatedCategory = categoryService.updateCategory(id, categoryDetails);
            logger.info("END updateCategory - Category updated with ID: {}", updatedCategory.getId());
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in updateCategory - Category not found with ID: {}", id, e);
            throw e;
        }
    }

    // Actualizar parcialmente una categoría por ID
    @PatchMapping("/{id}")
    public ResponseEntity<EventCategory> updateEventCategory(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("BEGIN updateEventCategoryPartial - Partially updating category with ID: {}", id);
        try {
            EventCategory updatedCategory = categoryService.updateEventCategoryPartial(id, updates);
            logger.info("END updateEventCategoryPartial - Category updated with ID: {}", updatedCategory.getId());
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            logger.error("Error in updateEventCategoryPartial - Category not found with ID: {}", id, e);
            throw e;
        }
    }

    // Eliminar una categoría por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        logger.info("BEGIN deleteCategory - Deleting category with ID: {}", id);
        try {
            categoryService.deleteCategory(id);
            logger.info("END deleteCategory - Category deleted with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error in deleteCategory - Category not found with ID: {}", id, e);
            throw e;
        }
    }

    // Manejar excepciones de categoría no encontrada
    @ExceptionHandler(EventCategoryNotFoundException.class)
    public ResponseEntity<String> handleEventCategoryNotFoundException(EventCategoryNotFoundException exception) {
        logger.error("Handling EventCategoryNotFoundException - {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
