package com.svalero.apievents;

import com.svalero.apievents.domain.Payment;
import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.exception.PaymentNotFoundException;
import com.svalero.apievents.repository.PaymentRepository;
import com.svalero.apievents.service.PaymentService;
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
public class PaymentServiceTests {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        payment = new Payment();
        payment.setId(1L);
        payment.setName("Test Payment");
        payment.setCustomerName("John Doe");
        payment.setAmount(200.0);
        payment.setStatus("PAID");
        payment.setPaymentDate(LocalDate.of(2025, 3, 18));
        payment.setReservation(reservation);
    }

    @Test
    void testGetAllPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getAllPayments();

        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testSavePayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment saved = paymentService.savePayment(payment);

        assertNotNull(saved);
        assertEquals("Test Payment", saved.getName());
        assertEquals("John Doe", saved.getCustomerName());
        verify(paymentRepository).save(payment);
    }

    @Test
    void testGetPaymentByIdFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Payment found = paymentService.getPaymentById(1L);

        assertNotNull(found);
        assertEquals(200.0, found.getAmount());
        verify(paymentRepository).findById(1L);
    }

    @Test
    void testGetPaymentByIdNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(99L));
    }

    @Test
    void testUpdatePayment() {
        Payment updatedDetails = new Payment();
        updatedDetails.setName("Updated");
        updatedDetails.setCustomerName("Jane Doe");
        updatedDetails.setAmount(300.0);
        updatedDetails.setStatus("PENDING");
        updatedDetails.setPaymentDate(LocalDate.of(2025, 4, 1));
        updatedDetails.setReservation(payment.getReservation());

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedDetails);

        Payment result = paymentService.updatePayment(1L, updatedDetails);

        assertEquals("Updated", result.getName());
        assertEquals("Jane Doe", result.getCustomerName());
        assertEquals(300.0, result.getAmount());
    }

    @Test
    void testUpdatePaymentPartial() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "CANCELLED");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.updatePaymentPartial(1L, updates);

        assertEquals("CANCELLED", result.getStatus());
    }

    @Test
    void testDeletePaymentSuccess() {
        when(paymentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(paymentRepository).deleteById(1L);

        paymentService.deletePayment(1L);

        verify(paymentRepository).deleteById(1L);
    }

    @Test
    void testDeletePaymentNotFound() {
        when(paymentRepository.existsById(2L)).thenReturn(false);

        assertThrows(PaymentNotFoundException.class, () -> paymentService.deletePayment(2L));
    }

    @Test
    void testGetPaymentsByDate() {
        when(paymentRepository.findByPaymentDate(any(LocalDate.class))).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getPaymentsByDate(LocalDate.now());

        assertEquals(1, result.size());
    }

    @Test
    void testGetPaymentsBetweenDates() {
        when(paymentRepository.findByPaymentDateBetween(any(), any())).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getPaymentsBetweenDates(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        assertEquals(1, result.size());
    }

    @Test
    void testGetPaymentsByStatus() {
        when(paymentRepository.findByStatus("PAID")).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getPaymentsByStatus("PAID");

        assertEquals(1, result.size());
        assertEquals("PAID", result.get(0).getStatus());
    }

    @Test
    void testGetPaymentsByAmount() {
        when(paymentRepository.findByAmountGreaterThanEqual(150.0)).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getPaymentsByAmount(150.0);

        assertEquals(1, result.size());
    }

    @Test
    void testGetPaymentsByReservation() {
        when(paymentRepository.findByReservationId(1L)).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getPaymentsByReservation(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getReservation().getId());
    }
}
