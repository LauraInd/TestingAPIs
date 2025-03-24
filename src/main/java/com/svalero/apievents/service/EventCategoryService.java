package com.svalero.apievents.service;

import com.svalero.apievents.domain.EventCategory;
import com.svalero.apievents.exception.EventCategoryNotFoundException;
import com.svalero.apievents.repository.EventCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class EventCategoryService {

    private final EventCategoryRepository categoryRepository;

    @Autowired
    public EventCategoryService(EventCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Obtener todas las categorías
    public List<EventCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Buscar categorías por nombre
    public List<EventCategory> getCategoriesByName(String name) {
        return categoryRepository.findByNameContaining(name);
    }

    // Buscar categorías por descripción
    public List<EventCategory> getCategoriesByDescription(String description) {
        return categoryRepository.findByDescriptionContaining(description);
    }

    // Buscar categorías activas
    public List<EventCategory> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }

    // Buscar categorías inactivas
    public List<EventCategory> getInactiveCategories() {
        return categoryRepository.findByActiveFalse();
    }

    // Buscar categorías por fecha de creación
    public List<EventCategory> getCategoriesByCreationDate(LocalDate date) {
        return categoryRepository.findByCreationDate(date);
    }

    // Buscar categorías creadas después de una fecha específica
    public List<EventCategory> getCategoriesCreatedAfter(LocalDate date) {
        return categoryRepository.findByCreationDateAfter(date);
    }

    // Buscar categorías creadas antes de una fecha específica
    public List<EventCategory> getCategoriesCreatedBefore(LocalDate date) {
        return categoryRepository.findByCreationDateBefore(date);
    }

    // Buscar categorías por número de eventos
    public List<EventCategory> getCategoriesByNumberOfEvents(int numberOfEvents) {
        return categoryRepository.findByNumberEvents(numberOfEvents);
    }

    // Buscar categorías con un número mínimo de eventos
    public List<EventCategory> getCategoriesWithMinEvents(int minEvents) {
        return categoryRepository.findByNumberEventsGreaterThanEqual(minEvents);
    }

    // Guardar una nueva categoría
    public EventCategory saveCategory(EventCategory category) {
        return categoryRepository.save(category);
    }

    // Obtener una categoría por ID
    public EventCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EventCategoryNotFoundException("Category not found with id: " + id));
    }

    // Actualizar una categoría por ID
    public EventCategory updateCategory(Long id, EventCategory categoryDetails) {
        EventCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EventCategoryNotFoundException("Category not found with id: " + id));

        // Actualizar los campos de la categoría
        existingCategory.setName(categoryDetails.getName());
        existingCategory.setDescription(categoryDetails.getDescription());
        existingCategory.setCreationDate(categoryDetails.getCreationDate());
        existingCategory.setNumberEvents(categoryDetails.getNumberEvents());
        existingCategory.setActive(categoryDetails.getActive());

        return categoryRepository.save(existingCategory);
    }

    public EventCategory updateEventCategoryPartial(Long id, Map<String, Object> updates) {
        EventCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event Category not found with id: " + id));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(EventCategory.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, existingCategory, value);
            }
        });

        return categoryRepository.save(existingCategory);
    }

    // Eliminar una categoría por ID
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EventCategoryNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
