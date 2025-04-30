package com.svalero.apievents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.controller.ReservationController;
import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.exception.ReservationNotFoundException;
import com.svalero.apievents.service.EventService;
import com.svalero.apievents.service.ReservationService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
public class ReservationControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private EventService eventService;

    @Test
    public void testGetAllReservations_ReturnsOk() throws Exception {
        List<Reservation> reservations = List.of(
                new Reservation(1L, "Concert", "Alice", "alice@example.com", LocalDate.now(), 2, new Event()),
                new Reservation(2L, "Conference", "Bob", "bob@example.com", LocalDate.now(), 3, new Event())
        );

        when(reservationService.getAllReservations()).thenReturn(reservations);

        MvcResult result = mockMvc.perform(get("/reservations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> responseList = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals("Alice", responseList.get(0).getCustomerName());
    }

    @Test
    public void testGetReservationById_ReturnsOk() throws Exception {
        Reservation reservation = new Reservation(1L, "Concert", "Alice", "alice@example.com",
                LocalDate.of(2024, 6, 1), 2, new Event());

        when(reservationService.getReservationById(1L)).thenReturn(reservation);

        MvcResult result = mockMvc.perform(get("/reservations/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Reservation response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals("Alice", response.getCustomerName());
        assertEquals(2, response.getQuantity());
    }

    @Test
    public void testGetReservationById_NotFound() throws Exception {
        when(reservationService.getReservationById(99L))
                .thenThrow(new ReservationNotFoundException("Reservation not found with id: 99"));

        mockMvc.perform(get("/reservations/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Reservation not found with id: 99"));
    }

    @Test
    public void testAddReservation_ReturnsCreated() throws Exception {
        Reservation reservation = new Reservation(null, "Concert", "Alice", "alice@example.com",
                LocalDate.of(2024, 6, 1), 2, new Event());
        Reservation saved = new Reservation(1L, "Concert", "Alice", "alice@example.com",
                LocalDate.of(2024, 6, 1), 2, new Event());

        when(reservationService.saveReservation(any(Reservation.class))).thenReturn(saved);

        String body = objectMapper.writeValueAsString(reservation);

        MvcResult result = mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        Reservation response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals("Alice", response.getCustomerName());
        assertEquals(2, response.getQuantity());
    }

    @Test
    public void testDeleteReservation_ReturnsNoContent() throws Exception {
        doNothing().when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/reservations/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateReservationPartial_ReturnsOk() throws Exception {
        Reservation updated = new Reservation(1L, "Updated Event", "Alice", "alice@example.com",
                LocalDate.of(2024, 6, 2), 4, new Event());

        Map<String, Object> patchData = Map.of("quantity", 4, "reservationDate", "2024-06-02");

        when(reservationService.updateReservationPartial(eq(1L), anyMap())).thenReturn(updated);

        String patchJson = objectMapper.writeValueAsString(patchData);

        MvcResult result = mockMvc.perform(patch("/reservations/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andReturn();

        Reservation response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(4, response.getQuantity());
        assertEquals(LocalDate.of(2024, 6, 2), response.getReservationDate());
    }
}
