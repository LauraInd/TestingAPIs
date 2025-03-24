package com.svalero.apievents;

import com.svalero.apievents.domain.Event;
import com.svalero.apievents.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EventRepositoryTests {

    @Autowired
    private EventRepository eventRepository;

    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setEventName("Festival de Música");
        event.setCapacity(500);
        event.setEventDate(LocalDate.of(2025, 5, 20));
        event.setUbication("Madrid");
        eventRepository.save(event);
    }

    @Test
    void testFindAll() {
        List<Event> events = eventRepository.findAll();
        assertFalse(events.isEmpty());
        assertEquals("Festival de Música", events.get(0).getEventName());
    }

    @Test
    void testFindByEventNameContaining() {
        List<Event> events = eventRepository.findByEventNameContaining("Música");
        assertEquals(1, events.size());
        assertEquals("Festival de Música", events.get(0).getEventName());
    }

    @Test
    void testFindByCapacityLessThanEqual() {
        List<Event> events = eventRepository.findByCapacityLessThanEqual(600);
        assertEquals(1, events.size());
        assertTrue(events.get(0).getCapacity() <= 600);
    }

    @Test
    void testFindByEventDate() {
        List<Event> events = eventRepository.findByEventDate(LocalDate.of(2025, 5, 20));
        assertEquals(1, events.size());
        assertEquals("Festival de Música", events.get(0).getEventName());
    }

    @Test
    void testFindByEventDateBetween() {
        List<Event> events = eventRepository.findByEventDateBetween(
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 30));
        assertEquals(1, events.size());
    }

    @Test
    void testFindByUbicationContaining() {
        List<Event> events = eventRepository.findByUbicationContaining("Madrid");
        assertEquals(1, events.size());
        assertEquals("Madrid", events.get(0).getUbication());
    }
}