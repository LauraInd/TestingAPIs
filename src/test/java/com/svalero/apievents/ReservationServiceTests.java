package com.svalero.apievents;

import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.exception.ReservationNotFoundException;
import com.svalero.apievents.repository.ReservationRepository;
import com.svalero.apievents.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTests {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        Event event = new Event();
        event.setId(1L);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setName("Reserva Test");
        reservation.setCustomerName("Carlos Perez");
        reservation.setEmail("carlos@example.com");
        reservation.setReservationDate(LocalDate.now());
        reservation.setQuantity(2);
        reservation.setEvent(event);
    }

    @Test
    void testGetAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<Reservation> results = reservationService.getAllReservations();

        assertEquals(1, results.size());
        verify(reservationRepository).findAll();
    }

    @Test
    void testGetReservationByIdFound() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals("Carlos Perez", result.getCustomerName());
    }

    @Test
    void testGetReservationByIdNotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> reservationService.getReservationById(99L));
    }

    @Test
    void testSaveReservation() {
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation saved = reservationService.saveReservation(reservation);

        assertEquals("Carlos Perez", saved.getCustomerName());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testUpdateReservation() {
        Reservation updated = new Reservation();
        updated.setCustomerName("Luis Garcia");
        updated.setEmail("luis@example.com");
        updated.setReservationDate(LocalDate.now().plusDays(1));
        updated.setQuantity(4);
        updated.setEvent(reservation.getEvent());

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(updated);

        Reservation result = reservationService.updateReservation(1L, updated);

        assertEquals("Luis Garcia", result.getCustomerName());
        assertEquals(4, result.getQuantity());
    }

    @Test
    void testUpdateReservationPartial() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", 10);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);

        Reservation result = reservationService.updateReservationPartial(1L, updates);

        assertEquals(10, result.getQuantity());
    }

    @Test
    void testDeleteReservationSuccess() {
        when(reservationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reservationRepository).deleteById(1L);

        reservationService.deleteReservation(1L);

        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void testDeleteReservationNotFound() {
        when(reservationRepository.existsById(2L)).thenReturn(false);

        assertThrows(ReservationNotFoundException.class, () -> reservationService.deleteReservation(2L));
    }

    @Test
    void testGetReservationsByCustomerName() {
        when(reservationRepository.findByCustomerNameContaining("Carlos"))
                .thenReturn(List.of(reservation));

        List<Reservation> results = reservationService.getReservationsByCustomerName("Carlos");

        assertEquals(1, results.size());
    }

    @Test
    void testGetReservationsByDate() {
        when(reservationRepository.findByReservationDate(LocalDate.now()))
                .thenReturn(List.of(reservation));

        List<Reservation> results = reservationService.getReservationsByDate(LocalDate.now());

        assertEquals(1, results.size());
    }

    @Test
    void testGetReservationsBetweenDates() {
        when(reservationRepository.findByReservationDateBetween(any(), any()))
                .thenReturn(List.of(reservation));

        List<Reservation> results = reservationService.getReservationsBetweenDates(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        assertEquals(1, results.size());
    }

    @Test
    void testGetReservationsByQuantity() {
        when(reservationRepository.findByQuantity(2)).thenReturn(List.of(reservation));

        List<Reservation> results = reservationService.getReservationsByQuantity(2);

        assertEquals(1, results.size());
    }

    @Test
    void testGetReservationsByEvent() {
        when(reservationRepository.findByEventId(1L)).thenReturn(List.of(reservation));

        List<Reservation> results = reservationService.getReservationsByEvent(1L);

        assertEquals(1, results.size());
    }
}
