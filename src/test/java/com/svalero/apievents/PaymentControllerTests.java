package com.svalero.apievents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.controller.PaymentController;
import com.svalero.apievents.domain.Payment;
import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.service.PaymentService;
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

@WebMvcTest(PaymentController.class)
public class PaymentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Payment payment;

    @BeforeEach
    void setUp() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        payment = new Payment();
        payment.setId(1L);
        payment.setAmount(100.0);
        payment.setStatus("PAID");
        payment.setPaymentDate(LocalDate.now());
        payment.setReservation(reservation);
    }

    @Test
    void testGetAllPayments() throws Exception {
        List<Payment> mockPayments = List.of(payment);
        Mockito.when(paymentService.getAllPayments()).thenReturn(mockPayments);

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0));

        assertEquals(1, mockPayments.size());
        assertEquals("PAID", mockPayments.get(0).getStatus());
    }

    @Test
    void testAddPayment() throws Exception {
        Mockito.when(paymentService.savePayment(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.0));

        assertEquals(1L, payment.getId());
    }

    @Test
    void testGetPaymentById() throws Exception {
        Mockito.when(paymentService.getPaymentById(1L)).thenReturn(payment);

        mockMvc.perform(get("/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        assertEquals(100.0, payment.getAmount());
    }

    @Test
    void testUpdatePayment() throws Exception {
        Mockito.when(paymentService.updatePayment(eq(1L), any(Payment.class))).thenReturn(payment);

        mockMvc.perform(put("/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        assertEquals("PAID", payment.getStatus());
    }

    @Test
    void testGetPaymentsByDate() throws Exception {
        Mockito.when(paymentService.getPaymentsByDate(any(LocalDate.class))).thenReturn(List.of(payment));

        mockMvc.perform(get("/payments/date")
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0));
    }

    @Test
    void testGetPaymentsBetweenDates() throws Exception {
        Mockito.when(paymentService.getPaymentsBetweenDates(any(), any())).thenReturn(List.of(payment));

        mockMvc.perform(get("/payments/range")
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPaymentsByStatus() throws Exception {
        Mockito.when(paymentService.getPaymentsByStatus("PAID")).thenReturn(List.of(payment));

        mockMvc.perform(get("/payments/status")
                        .param("status", "PAID"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPaymentsByAmount() throws Exception {
        Mockito.when(paymentService.getPaymentsByAmount(100.0)).thenReturn(List.of(payment));

        mockMvc.perform(get("/payments/amount")
                        .param("amount", "100.0"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPaymentsByReservation() throws Exception {
        Mockito.when(paymentService.getPaymentsByReservation(1L)).thenReturn(List.of(payment));

        mockMvc.perform(get("/payments/reservation")
                        .param("reservationId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        assertEquals(1L, payment.getReservation().getId());
    }

    @Test
    void testUpdatePaymentPartial() throws Exception {
        Mockito.when(paymentService.updatePaymentPartial(eq(1L), any(Map.class))).thenReturn(payment);

        mockMvc.perform(patch("/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "PENDING"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        assertEquals("PAID", payment.getStatus());
    }

    @Test
    void testDeletePayment() throws Exception {
        Mockito.doNothing().when(paymentService).deletePayment(1L);

        mockMvc.perform(delete("/payments/1"))
                .andExpect(status().isNoContent());
    }
}
