package com.svalero.apievents.repository;

import com.svalero.apievents.domain.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    // Método para obtener todos los eventos
    List<Event> findAll();

    // Método para buscar eventos por nombre
    List<Event> findByEventNameContaining(String eventName);

    // Método para buscar eventos con capacidad menor o igual a un valor dado
    List<Event> findByCapacityLessThanEqual(int capacity);

    // Método para buscar eventos por fecha
    List<Event> findByEventDate(LocalDate eventDate);

    // Método para buscar eventos entre dos fechas
    List<Event> findByEventDateBetween(LocalDate startDate, LocalDate endDate);

    // Método para buscar eventos por ubicación
    List<Event> findByUbicationContaining(String ubication);
}
