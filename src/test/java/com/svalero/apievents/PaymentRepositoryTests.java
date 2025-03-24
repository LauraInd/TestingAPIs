package com.svalero.apievents;

import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.Payment;
import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.repository.EventRepository;
import com.svalero.apievents.repository.PaymentRepository;
import com.svalero.apievents.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PaymentRepositoryTests {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventRepository eventRepository;

    private Payment payment;

    @BeforeEach
    void setUp() {
        Event event = new Event();
        event.setEventName("Test Event");
        event.setCapacity(100);
        event.setEventDate(LocalDate.of(2025, 5, 20));
        event.setUbication("Madrid");
        event = eventRepository.save(event);

        Reservation reservation = new Reservation();
        reservation.setName("Reservation 1");
        reservation.setCustomerName("Jane Doe");
        reservation.setEmail("jane@example.com");
        reservation.setReservationDate(LocalDate.now());
        reservation.setQuantity(2);
        reservation.setEvent(event);
        reservation = reservationRepository.save(reservation);

        payment = new Payment();
        payment.setName("Test Payment");
        payment.setCustomerName("John Doe");
        payment.setAmount(150.0);
        payment.setStatus("PAID");
        payment.setPaymentDate(LocalDate.of(2025, 3, 18));
        payment.setReservation(reservation);

        paymentRepository.save(payment);
    }

    @Test
    void testFindAll() {
        List<Payment> payments = paymentRepository.findAll();
        assertFalse(payments.isEmpty());
        assertEquals("PAID", payments.get(0).getStatus());
    }

    @Test
    void testFindByPaymentDate() {
        List<Payment> payments = paymentRepository.findByPaymentDate(LocalDate.of(2025, 3, 18));
        assertEquals(1, payments.size());
        assertEquals(150.0, payments.get(0).getAmount());
    }

    @Test
    void testFindByPaymentDateBetween() {
        List<Payment> payments = paymentRepository.findByPaymentDateBetween(
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 31));
        assertEquals(1, payments.size());
    }

    @Test
    void testFindByStatus() {
        List<Payment> payments = paymentRepository.findByStatus("PAID");
        assertEquals(1, payments.size());
        assertEquals("PAID", payments.get(0).getStatus());
    }

    @Test
    void testFindByAmountGreaterThanEqual() {
        List<Payment> payments = paymentRepository.findByAmountGreaterThanEqual(100.0);
        assertEquals(1, payments.size());
        assertTrue(payments.get(0).getAmount() >= 100.0);
    }

    @Test
    void testFindByReservationId() {
        List<Payment> payments = paymentRepository.findByReservationId(
                payment.getReservation().getId());
        assertEquals(1, payments.size());
        assertEquals("John Doe", payments.get(0).getCustomerName());
    }
}


