package com.svalero.apievents.controller;

import com.svalero.apievents.domain.Payment;
import com.svalero.apievents.exception.PaymentNotFoundException;
import com.svalero.apievents.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Obtener todos los pagos
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        logger.info("BEGIN getAllPayments");
        List<Payment> payments = paymentService.getAllPayments();
        logger.info("END getAllPayments - Total payments fetched: {}", payments.size());
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Guardar un nuevo pago
    @PostMapping
    public ResponseEntity<Payment> addPayment(@RequestBody Payment payment) {
        logger.info("BEGIN addPayment - Adding payment for reservation ID: {}", payment.getReservation().getId());
        Payment newPayment = paymentService.savePayment(payment);
        logger.info("END addPayment - Payment added with ID: {}", newPayment.getId());
        return new ResponseEntity<>(newPayment, HttpStatus.CREATED);
    }

    // Obtener un pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        logger.info("BEGIN getPaymentById - Fetching payment with ID: {}", id);
        try {
            Payment payment = paymentService.getPaymentById(id);
            logger.info("END getPaymentById - Payment found: {}", payment.getId());
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getPaymentById - Payment not found with ID: {}", id, e);
            throw e;
        }
    }

    // Actualizar un pago por ID
    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment paymentDetails) {
        logger.info("BEGIN updatePayment - Updating payment with ID: {}", id);
        try {
            Payment updatedPayment = paymentService.updatePayment(id, paymentDetails);
            logger.info("END updatePayment - Payment updated with ID: {}", updatedPayment.getId());
            return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in updatePayment - Payment not found with ID: {}", id, e);
            throw e;
        }
    }

    // Buscar pagos por fecha
    @GetMapping("/date")
    public ResponseEntity<List<Payment>> getPaymentsByDate(@RequestParam LocalDate date) {
        logger.info("BEGIN getPaymentsByDate - Searching payments for date: {}", date);
        List<Payment> payments = paymentService.getPaymentsByDate(date);
        logger.info("END getPaymentsByDate - Total payments found: {}", payments.size());
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Buscar pagos entre dos fechas
    @GetMapping("/range")
    public ResponseEntity<List<Payment>> getPaymentsBetweenDates(
            @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        logger.info("BEGIN getPaymentsBetweenDates - Searching payments between {} and {}", startDate, endDate);
        List<Payment> payments = paymentService.getPaymentsBetweenDates(startDate, endDate);
        logger.info("END getPaymentsBetweenDates - Total payments found: {}", payments.size());
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Buscar pagos por estado
    @GetMapping("/status")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@RequestParam String status) {
        logger.info("BEGIN getPaymentsByStatus - Searching payments with status: {}", status);
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        logger.info("END getPaymentsByStatus - Total payments found: {}", payments.size());
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Buscar pagos por monto mínimo
    @GetMapping("/amount")
    public ResponseEntity<List<Payment>> getPaymentsByAmount(@RequestParam double amount) {
        logger.info("BEGIN getPaymentsByAmount - Searching payments with minimum amount: {}", amount);
        List<Payment> payments = paymentService.getPaymentsByAmount(amount);
        logger.info("END getPaymentsByAmount - Total payments found: {}", payments.size());
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Buscar pagos por ID de reserva
    @GetMapping("/reservation")
    public ResponseEntity<List<Payment>> getPaymentsByReservation(@RequestParam Long reservationId) {
        logger.info("BEGIN getPaymentsByReservation - Searching payments for reservation ID: {}", reservationId);
        List<Payment> payments = paymentService.getPaymentsByReservation(reservationId);
        logger.info("END getPaymentsByReservation - Total payments found: {}", payments.size());
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Actualizar parcialmente un pago por ID
    @PatchMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("BEGIN updatePaymentPartial - Partially updating payment with ID: {}", id);
        try {
            Payment updatedPayment = paymentService.updatePaymentPartial(id, updates);
            logger.info("END updatePaymentPartial - Payment updated with ID: {}", updatedPayment.getId());
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            logger.error("Error in updatePaymentPartial - Payment not found with ID: {}", id, e);
            throw e;
        }
    }

    // Eliminar un pago por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        logger.info("BEGIN deletePayment - Deleting payment with ID: {}", id);
        try {
            paymentService.deletePayment(id);
            logger.info("END deletePayment - Payment deleted with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error in deletePayment - Payment not found with ID: {}", id, e);
            throw e;
        }
    }

    // Manejar excepciones
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(PaymentNotFoundException exception) {
        logger.error("Handling PaymentNotFoundException - {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}

