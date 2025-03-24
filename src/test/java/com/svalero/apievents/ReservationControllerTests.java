package com.svalero.apievents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.service.ReservationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        Event event = new Event();
        event.setId(1L);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setName("ReservaTest");
        reservation.setCustomerName("Carlos Perez");
        reservation.setEmail("carlos@example.com");
        reservation.setReservationDate(LocalDate.now());
        reservation.setQuantity(2);
        reservation.setEvent(event);
    }

    @Test
    void testGetAllReservations() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(List.of(reservation));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Carlos Perez"));
    }

    @Test
    void testAddReservation() throws Exception {
        when(reservationService.saveReservation(any())).thenReturn(reservation);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetReservationsByCustomerName() throws Exception {
        when(reservationService.getReservationsByCustomerName("Carlos"))
                .thenReturn(List.of(reservation));

        mockMvc.perform(get("/reservations/customer")
                        .param("name", "Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Carlos Perez"));
    }

    @Test
    void testGetReservationById() throws Exception {
        when(reservationService.getReservationById(1L)).thenReturn(reservation);

        mockMvc.perform(get("/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateReservation() throws Exception {
        when(reservationService.updateReservation(eq(1L), any())).thenReturn(reservation);

        mockMvc.perform(put("/reservations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Carlos Perez"));
    }

    @Test
    void testDeleteReservation() throws Exception {
        doNothing().when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/reservations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetReservationsByDate() throws Exception {
        when(reservationService.getReservationsByDate(LocalDate.now())).thenReturn(List.of(reservation));

        mockMvc.perform(get("/reservations/date")
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetReservationsBetweenDates() throws Exception {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        when(reservationService.getReservationsBetweenDates(start, end)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/reservations/range")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetReservationsByQuantity() throws Exception {
        when(reservationService.getReservationsByQuantity(2)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/reservations/quantity")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    void testGetReservationsByEvent() throws Exception {
        when(reservationService.getReservationsByEvent(1L)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/reservations/event")
                        .param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].event.id").value(1));
    }
}

