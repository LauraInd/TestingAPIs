package com.svalero.apievents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.apievents.controller.PaymentController;
import com.svalero.apievents.domain.Payment;
import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.exception.PaymentNotFoundException;
import com.svalero.apievents.service.PaymentService;
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

@WebMvcTest(PaymentController.class)
public class PaymentsControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    void testGetAllPayments_ReturnsOk() throws Exception {
        Payment payment1 = new Payment(1L, "Pago A", "Alice", LocalDate.now(), 100.0, "PAID", new Reservation());
        Payment payment2 = new Payment(2L, "Pago B", "Bob", LocalDate.now(), 150.0, "PENDING", new Reservation());

        when(paymentService.getAllPayments()).thenReturn(List.of(payment1, payment2));

        MvcResult result = mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andReturn();

        List<Payment> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(2, response.size());
    }

    @Test
    void testGetPaymentById_ReturnsOk() throws Exception {
        Payment payment = new Payment(1L, "Pago A", "Alice", LocalDate.now(), 100.0, "PAID", new Reservation());
        when(paymentService.getPaymentById(1L)).thenReturn(payment);

        mockMvc.perform(get("/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pago A"));

        verify(paymentService, times(1)).getPaymentById(1L);
    }

    @Test
    void testGetPaymentById_NotFound() throws Exception {
        when(paymentService.getPaymentById(99L)).thenThrow(new PaymentNotFoundException("Payment not found"));

        mockMvc.perform(get("/payments/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Payment not found"));
    }

    @Test
    void testAddPayment_ReturnsCreated() throws Exception {
        Payment payment = new Payment(null, "Pago A", "Alice", LocalDate.now(), 120.0, "PAID", new Reservation());
        Payment saved   = new Payment(1L,   "Pago A", "Alice", LocalDate.now(), 120.0, "PAID", new Reservation());

        when(paymentService.savePayment(any(Payment.class))).thenReturn(saved);

        MvcResult result = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        Payment response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals("Pago A", response.getName());
    }

    @Test
    void testUpdatePayment_ReturnsOk() throws Exception {
        Payment updated = new Payment(1L, "Pago Actualizado","Alice", LocalDate.now(), 150.0, "PAID", new Reservation());

        when(paymentService.updatePayment(eq(1L), any(Payment.class))).thenReturn(updated);

        mockMvc.perform(put("/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pago Actualizado"));
    }

    @Test
    void testUpdatePaymentPartial_ReturnsOk() throws Exception {
        Payment updated = new Payment(1L, "Pago Parcial","Alice", LocalDate.now(), 200.0, "PAID", new Reservation());

        Map<String, Object> updates = Map.of("amount", 200.0);
        when(paymentService.updatePaymentPartial(eq(1L), anyMap())).thenReturn(updated);

        mockMvc.perform(patch("/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.0));
    }

    @Test
    void testDeletePayment_ReturnsNoContent() throws Exception {
        doNothing().when(paymentService).deletePayment(1L);

        mockMvc.perform(delete("/payments/1"))
                .andExpect(status().isNoContent());

        verify(paymentService, times(1)).deletePayment(1L);
    }

    @Test
    void testDeletePayment_NotFound() throws Exception {
        doThrow(new PaymentNotFoundException("Payment not found")).when(paymentService).deletePayment(99L);

        mockMvc.perform(delete("/payments/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Payment not found"));
    }
}
