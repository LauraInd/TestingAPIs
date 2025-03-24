package com.svalero.apievents.repository;

import com.svalero.apievents.domain.EventCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventCategoryRepository extends CrudRepository<EventCategory, Long> {

    // Método para obtener todas las categorías
    List<EventCategory> findAll();

    // Método para buscar categorías por nombre (parcial o completo)
    List<EventCategory> findByNameContaining(String name);

    // Método para buscar categorías por descripcion
    List<EventCategory> findByDescriptionContaining(String description);
    // Método para buscar categorías activas
    List<EventCategory> findByActiveTrue();

    // Método para buscar categorías inactivas
    List<EventCategory> findByActiveFalse();

    // Método para buscar categorías por fecha de creación exacta
    List<EventCategory> findByCreationDate(LocalDate creationDate);

    // Método para buscar categorías creadas después de una fecha específica
    List<EventCategory> findByCreationDateAfter(LocalDate date);

    // Método para buscar categorías creadas antes de una fecha específica
    List<EventCategory> findByCreationDateBefore(LocalDate date);

    // Método para buscar categorías con un número específico de eventos
    List<EventCategory> findByNumberEvents(int numberEvents);

    // Método para buscar categorías con más de un cierto número de eventos
    List<EventCategory> findByNumberEventsGreaterThanEqual(int numberEvents);
}
