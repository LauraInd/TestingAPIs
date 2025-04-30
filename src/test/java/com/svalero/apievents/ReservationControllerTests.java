package com.svalero.apievents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.controller.ReservationController;
import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.exception.ReservationNotFoundException;
import com.svalero.apievents.service.EventService;
import com.svalero.apievents.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private Reservation reservation1;
    private Reservation reservation2;

    @BeforeEach
    void setUp() {
        Event event = new Event();
        reservation1 = new Reservation(1L, "Concert", "Alice", "alice@example.com", LocalDate.of(2024, 6, 1), 2, event);
        reservation2 = new Reservation(2L, "Conference", "Bob", "bob@example.com", LocalDate.of(2024, 7, 15), 3, event);
    }

    @Test
    void testGetAllReservations() throws Exception {
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);
        when(reservationService.getAllReservations()).thenReturn(reservations);

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].customerName").value("Alice"));

        verify(reservationService, times(1)).getAllReservations();
    }

    @Test
    void testAddReservation() throws Exception {
        when(reservationService.saveReservation(any(Reservation.class))).thenReturn(reservation1);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("Alice"));

        verify(reservationService, times(1)).saveReservation(any(Reservation.class));
    }

    @Test
    void testGetReservationById() throws Exception {
        when(reservationService.getReservationById(1L)).thenReturn(reservation1);

        mockMvc.perform(get("/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Alice"));

        verify(reservationService, times(1)).getReservationById(1L);
    }

    @Test
    void testGetReservationById_NotFound() throws Exception {
        when(reservationService.getReservationById(99L))
                .thenThrow(new ReservationNotFoundException("Reservation not found"));

        mockMvc.perform(get("/reservations/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Reservation not found"));

        verify(reservationService, times(1)).getReservationById(99L);
    }

    @Test
    void testUpdateReservation() throws Exception {
        when(reservationService.updateReservation(eq(1L), any(Reservation.class))).thenReturn(reservation1);

        mockMvc.perform(put("/reservations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Alice"));

        verify(reservationService, times(1)).updateReservation(eq(1L), any(Reservation.class));
    }

    @Test
    void testUpdateReservationPartial() throws Exception {
        when(reservationService.updateReservationPartial(eq(1L), any(Map.class))).thenReturn(reservation1);

        Map<String, Object> updates = Map.of("quantity", 4);

        mockMvc.perform(patch("/reservations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Alice"));

        verify(reservationService, times(1)).updateReservationPartial(eq(1L), any(Map.class));
    }

    @Test
    void testDeleteReservation() throws Exception {
        doNothing().when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/reservations/1"))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).deleteReservation(1L);
    }

    @Test
    void testDeleteReservation_NotFound() throws Exception {
        doThrow(new ReservationNotFoundException("Reservation not found")).when(reservationService).deleteReservation(99L);

        mockMvc.perform(delete("/reservations/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Reservation not found"));

        verify(reservationService, times(1)).deleteReservation(99L);
    }
}
