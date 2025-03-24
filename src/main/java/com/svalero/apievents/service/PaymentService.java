package com.svalero.apievents.service;

import com.svalero.apievents.domain.Payment;
import com.svalero.apievents.exception.PaymentNotFoundException;
import com.svalero.apievents.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Obtener todos los pagos
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Guardar un nuevo pago
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Obtener un pago por ID
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
    }

    // Actualizar un pago por ID
    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));

        // Actualizar los campos necesarios
        existingPayment.setName(paymentDetails.getName());
        existingPayment.setPaymentDate(paymentDetails.getPaymentDate());
        existingPayment.setAmount(paymentDetails.getAmount());
        existingPayment.setStatus(paymentDetails.getStatus());
        existingPayment.setReservation(paymentDetails.getReservation());

        return paymentRepository.save(existingPayment);
    }

    public Payment updatePaymentPartial(Long id, Map<String, Object> updates) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Payment.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, payment, value);
            }
        });

        return paymentRepository.save(payment);
    }

    // Buscar pagos por fecha
    public List<Payment> getPaymentsByDate(LocalDate date) {
        return paymentRepository.findByPaymentDate(date);
    }

    // Buscar pagos entre dos fechas
    public List<Payment> getPaymentsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }

    // Buscar pagos por estado
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    // Buscar pagos por monto mayor o igual
    public List<Payment> getPaymentsByAmount(double amount) {
        return paymentRepository.findByAmountGreaterThanEqual(amount);
    }

    // Buscar pagos por ID de reserva
    public List<Payment> getPaymentsByReservation(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId);
    }

    // Eliminar un pago
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new PaymentNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }
}
