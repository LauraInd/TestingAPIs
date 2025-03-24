package com.svalero.apievents;

import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.repository.EventRepository;
import com.svalero.apievents.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReservationRepositoryTests {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventRepository eventRepository;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        Event event = new Event();
        event.setEventName("Test Event");
        event.setCapacity(100);
        event.setEventDate(LocalDate.of(2025, 6, 15));
        event.setUbication("Madrid");
        event = eventRepository.save(event);

        reservation = new Reservation();
        reservation.setName("Reserva1");
        reservation.setCustomerName("Carlos Perez");
        reservation.setEmail("carlos@example.com");
        reservation.setReservationDate(LocalDate.now());
        reservation.setQuantity(3);
        reservation.setEvent(event);

        reservationRepository.save(reservation);
    }

    @Test
    void testFindAll() {
        List<Reservation> reservations = reservationRepository.findAll();
        assertFalse(reservations.isEmpty());
        assertEquals("Carlos Perez", reservations.get(0).getCustomerName());
    }

    @Test
    void testFindByCustomerNameContaining() {
        List<Reservation> results = reservationRepository.findByCustomerNameContaining("Carlos");
        assertEquals(1, results.size());
        assertTrue(results.get(0).getCustomerName().contains("Carlos"));
    }

    @Test
    void testFindByReservationDate() {
        List<Reservation> results = reservationRepository.findByReservationDate(LocalDate.now());
        assertEquals(1, results.size());
    }

    @Test
    void testFindByReservationDateBetween() {
        List<Reservation> results = reservationRepository.findByReservationDateBetween(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertEquals(1, results.size());
    }

    @Test
    void testFindByQuantity() {
        List<Reservation> results = reservationRepository.findByQuantity(3);
        assertEquals(1, results.size());
        assertEquals(3, results.get(0).getQuantity());
    }

    @Test
    void testFindByEventId() {
        List<Reservation> results = reservationRepository.findByEventId(reservation.getEvent().getId());
        assertEquals(1, results.size());
        assertEquals("Reserva1", results.get(0).getName());
    }
}
