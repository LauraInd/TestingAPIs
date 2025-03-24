package com.svalero.apievents;

import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.EventCategory;
import com.svalero.apievents.domain.dto.EventOutDto;
import com.svalero.apievents.domain.dto.EventRegistrationDto;
import com.svalero.apievents.exception.EventCategoryNotFoundException;
import com.svalero.apievents.exception.EventNotFoundException;
import com.svalero.apievents.repository.EventCategoryRepository;
import com.svalero.apievents.repository.EventRepository;
import com.svalero.apievents.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventCategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventCategory category;

    @BeforeEach
    void setUp() {
        category = new EventCategory();
        category.setId(1L);
        category.setName("Music");

        event = new Event();
        event.setId(1L);
        event.setEventName("Concert");
        event.setDescription("Live music");
        event.setEventDate(LocalDate.now());
        event.setCapacity(200);
        event.setUbication("Madrid");
        event.setCategory(category);
    }

    @Test
    void testGetAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(event));
        List<Event> result = eventService.getAllEvents();
        assertEquals(1, result.size());
    }

    @Test
    void testGetEventByIdFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        Event result = eventService.getEventById(1L);
        assertNotNull(result);
        assertEquals("Concert", result.getEventName());
    }

    @Test
    void testGetEventByIdNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.getEventById(99L));
    }

    @Test
    void testSaveEvent() {
        when(eventRepository.save(any())).thenReturn(event);
        Event saved = eventService.saveEvent(event);
        assertEquals("Concert", saved.getEventName());
    }

    @Test
    void testAddEventSuccess() {
        EventRegistrationDto dto = new EventRegistrationDto();
        dto.setEventName("Concert");
        dto.setCapacity(300);
        dto.setUbication("Madrid");
        dto.setCategoryId(1L);

        EventOutDto outDto = new EventOutDto();
        outDto.setId(1L);
        outDto.setEventName("Concert");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(dto, Event.class)).thenReturn(event);
        when(eventRepository.save(any())).thenReturn(event);
        when(modelMapper.map(event, EventOutDto.class)).thenReturn(outDto);

        EventOutDto result = eventService.add(dto);
        assertEquals("Concert", result.getEventName());
    }

    @Test
    void testAddEventCategoryNotFound() {
        EventRegistrationDto dto = new EventRegistrationDto();
        dto.setCategoryId(999L);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EventCategoryNotFoundException.class, () -> eventService.add(dto));
    }

    @Test
    void testUpdateEvent() {
        Event updated = new Event();
        updated.setEventName("Updated");
        updated.setDescription("New description");
        updated.setEventDate(LocalDate.now());
        updated.setCapacity(500);
        updated.setUbication("Barcelona");
        updated.setCategory(category);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(updated);

        Event result = eventService.updateEvent(1L, updated);
        assertEquals("Updated", result.getEventName());
    }

    @Test
    void testUpdateEventPartial() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("capacity", 400);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(event);

        Event result = eventService.updateEventPartial(1L, updates);
        assertEquals(400, result.getCapacity());
    }

    @Test
    void testDeleteEventSuccess() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(1L);
        eventService.deleteEvent(1L);
        verify(eventRepository).deleteById(1L);
    }

    @Test
    void testDeleteEventNotFound() {
        when(eventRepository.existsById(99L)).thenReturn(false);
        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(99L));
    }
}

