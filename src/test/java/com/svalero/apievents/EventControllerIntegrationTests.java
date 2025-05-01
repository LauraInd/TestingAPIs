package com.svalero.apievents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.controller.EventController;
import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.dto.EventOutDto;
import com.svalero.apievents.domain.dto.EventRegistrationDto;
import com.svalero.apievents.exception.EventNotFoundException;
import com.svalero.apievents.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    void testGetAllEvents_ReturnsOk() throws Exception {
        Event e1 = new Event(1L, "Festival", "Music festival", LocalDate.now(), 1000, "Madrid", 0.0, 0.0, null);
        Event e2 = new Event(2L, "Conferencia", "Tech talk", LocalDate.now(), 500, "Barcelona", 0.0, 0.0, null);


        when(eventService.getAllEvents()).thenReturn(List.of(e1, e2));

        MvcResult result = mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andReturn();

        List<Event> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(2, response.size());
    }

    @Test
    void testAddEvent_ReturnsCreated() throws Exception {
        EventRegistrationDto dto = new EventRegistrationDto(
                "Festival", LocalDate.now(), 200, "Valencia", 39.4699, -0.3763, 1L);
        EventOutDto outDto = new EventOutDto(
                1L,
                "Festival",
                LocalDate.now(),
                200,
                "Valencia",
                1L,
                39.4699,
                -0.3763
        );

        when(eventService.add(any(EventRegistrationDto.class))).thenReturn(outDto);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetEventById_ReturnsOk() throws Exception {
        Event event = new Event(
                1L,
                "Evento único",
                "Descripción",
                LocalDate.now(),
                300,
                "Sevilla",
                37.3826,
                -5.9963,
                null
        );
        when(eventService.getEventById(1L)).thenReturn(event);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Evento único"));
    }

    @Test
    void testGetEventById_NotFound() throws Exception {
        when(eventService.getEventById(99L)).thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(get("/events/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Event not found"));
    }

    @Test
    void testUpdateEvent_ReturnsOk() throws Exception {
        Event updated = new Event(1L, "Nuevo nombre", "Editado", LocalDate.now(), 400, "Zaragoza", 0.0, 0.0, null);
        when(eventService.updateEvent(eq(1L), any(Event.class))).thenReturn(updated);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Nuevo nombre"));
    }

    @Test
    void testUpdateEventPartial_ReturnsOk() throws Exception {
        Event partial = new Event(1L, "Parcial", "Editado", LocalDate.now(), 300, "Murcia", 0.0, 0.0, null);
        Map<String, Object> updates = Map.of("eventName", "Parcial");

        when(eventService.updateEventPartial(eq(1L), anyMap())).thenReturn(partial);

        mockMvc.perform(patch("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Parcial"));
    }

    @Test
    void testDeleteEvent_ReturnsNoContent() throws Exception {
        doNothing().when(eventService).deleteEvent(1L);

        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteEvent_NotFound() throws Exception {
        doThrow(new EventNotFoundException("Event not found")).when(eventService).deleteEvent(99L);

        mockMvc.perform(delete("/events/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Event not found"));
    }
}
