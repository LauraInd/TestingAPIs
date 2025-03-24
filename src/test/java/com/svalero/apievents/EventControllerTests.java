package com.svalero.apievents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.controller.EventController;
import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.dto.EventOutDto;
import com.svalero.apievents.domain.dto.EventRegistrationDto;
import com.svalero.apievents.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setEventName("Test Event");
        event.setCapacity(100);
        event.setEventDate(LocalDate.now());
        event.setUbication("Test City");
    }

    @Test
    void testGetAllEvents() throws Exception {
        Mockito.when(eventService.getAllEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName").value("Test Event"));
    }

    @Test
    void testAddEvent() throws Exception {
        EventOutDto dto = new EventOutDto();
        dto.setId(1L);
        dto.setEventName("Test Event");

        Mockito.when(eventService.add(any(EventRegistrationDto.class))).thenReturn(dto);

        EventRegistrationDto input = new EventRegistrationDto();
        input.setEventName("Test Event");

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetEventById() throws Exception {
        Mockito.when(eventService.getEventById(1L)).thenReturn(event);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Test Event"));

        assertEquals("Test Event", event.getEventName());
    }

    @Test
    void testGetEventsByName() throws Exception {
        Mockito.when(eventService.getEventsByName("Test"))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/events/name")
                        .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName").value("Test Event"));
    }

    @Test
    void testGetEventsByCapacity() throws Exception {
        Mockito.when(eventService.getEventsByCapacity(100))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/events/capacity")
                        .param("capacity", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].capacity").value(100));
    }

    @Test
    void testGetEventsByDate() throws Exception {
        Mockito.when(eventService.getEventsByDate(any(LocalDate.class))).thenReturn(List.of(event));

        mockMvc.perform(get("/events/date")
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName").value("Test Event"));
    }

    @Test
    void testGetEventsBetweenDates() throws Exception {
        Mockito.when(eventService.getEventsBetweenDates(any(), any())).thenReturn(List.of(event));

        mockMvc.perform(get("/events/range")
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetEventsByUbication() throws Exception {
        Mockito.when(eventService.getEventsByUbication("Test City")).thenReturn(List.of(event));

        mockMvc.perform(get("/events/ubication")
                        .param("ubication", "Test City"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ubication").value("Test City"));
    }

    @Test
    void testUpdateEvent() throws Exception {
        Mockito.when(eventService.updateEvent(eq(1L), any(Event.class))).thenReturn(event);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Test Event"));
    }

    @Test
    void testUpdateEventPartial() throws Exception {
        Mockito.when(eventService.updateEventPartial(eq(1L), any(Map.class))).thenReturn(event);

        mockMvc.perform(patch("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("eventName", "Updated Event"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Test Event"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        Mockito.doNothing().when(eventService).deleteEvent(1L);

        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isNoContent());
    }
}
