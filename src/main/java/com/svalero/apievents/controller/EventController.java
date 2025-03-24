package com.svalero.apievents.controller;

import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.dto.EventOutDto;
import com.svalero.apievents.domain.dto.EventRegistrationDto;
import com.svalero.apievents.exception.EventNotFoundException;
import com.svalero.apievents.service.EventService;
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
public class EventController {

    private final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Obtener todos los eventos
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        logger.info("BEGIN getAllEvents");
        List<Event> events = eventService.getAllEvents();
        logger.info("END getAllEvents - Total events fetched: {}", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Agregar un nuevo evento
    @PostMapping("/events")
    public ResponseEntity<EventOutDto> addEvent(@RequestBody EventRegistrationDto event) {
        logger.info("BEGIN addEvent - Adding new event: {}", event.getEventName());
        EventOutDto newEvent = eventService.add(event);
        logger.info("END addEvent - Event added with ID: {}", newEvent.getId());
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }



    // Buscar eventos por nombre
    @GetMapping("events/name")
    public ResponseEntity<List<Event>> getEventsByName(@RequestParam String name) {
        logger.info("BEGIN getEventsByName - Searching events with name: {}", name);
        List<Event> events = eventService.getEventsByName(name);
        logger.info("END getEventsByName - Total events found: {}", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Buscar eventos por capacidad máxima
    @GetMapping("events/capacity")
    public ResponseEntity<List<Event>> getEventsByCapacity(@RequestParam int capacity) {
        logger.info("BEGIN getEventsByCapacity - Searching events with capacity: {}", capacity);
        List<Event> events = eventService.getEventsByCapacity(capacity);
        logger.info("END getEventsByCapacity - Total events found: {}", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Buscar eventos por fecha
    @GetMapping("events/date")
    public ResponseEntity<List<Event>> getEventsByDate(@RequestParam LocalDate date) {
        logger.info("BEGIN getEventsByDate - Searching events for date: {}", date);
        List<Event> events = eventService.getEventsByDate(date);
        logger.info("END getEventsByDate - Total events found: {}", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Buscar eventos entre dos fechas
    @GetMapping("events/range")
    public ResponseEntity<List<Event>> getEventsBetweenDates(
            @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        logger.info("BEGIN getEventsBetweenDates - Searching events between {} and {}", startDate, endDate);
        List<Event> events = eventService.getEventsBetweenDates(startDate, endDate);
        logger.info("END getEventsBetweenDates - Total events found: {}", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Buscar eventos por ubicación
    @GetMapping("events/ubication")
    public ResponseEntity<List<Event>> getEventsByUbication(@RequestParam String ubication) {
        logger.info("BEGIN getEventsByUbication - Searching events in location: {}", ubication);
        List<Event> events = eventService.getEventsByUbication(ubication);
        logger.info("END getEventsByUbication - Total events found: {}", events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Obtener un evento por ID
    @GetMapping("events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) throws EventNotFoundException {
        logger.info("BEGIN getEventById - Fetching event with ID: {}", id);
        try {
            Event event = eventService.getEventById(id);
            logger.info("END getEventById - Event found: {}", event.getId());
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getEventById - Event not found with ID: {}", id, e);
            throw e;
        }
    }

    // Actualizar un evento por ID
    @PutMapping("events/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) throws EventNotFoundException {
        logger.info("BEGIN updateEvent - Updating event with ID: {}", id);
        try {
            Event updatedEvent = eventService.updateEvent(id, eventDetails);
            logger.info("END updateEvent - Event updated with ID: {}", updatedEvent.getId());
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in updateEvent - Event not found with ID: {}", id, e);
            throw e;
        }
    }

    // Actualizar parcialmente un evento por ID
    @PatchMapping("events/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("BEGIN updateEventPartial - Partially updating event with ID: {}", id);
        try {
            Event updatedEvent = eventService.updateEventPartial(id, updates);
            logger.info("END updateEventPartial - Event updated with ID: {}", updatedEvent.getId());
            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            logger.error("Error in updateEventPartial - Event not found with ID: {}", id, e);
            throw e;
        }
    }

    // Eliminar un evento por ID
    @DeleteMapping("events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) throws EventNotFoundException {
        logger.info("BEGIN deleteEvent - Deleting event with ID: {}", id);
        try {
            eventService.deleteEvent(id);
            logger.info("END deleteEvent - Event deleted with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error in deleteEvent - Event not found with ID: {}", id, e);
            throw e;
        }
    }

    // Manejar excepciones de evento no encontrado
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<String> handleEventNotFoundException(EventNotFoundException exception) {
        logger.error("Handling EventNotFoundException - {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}

