package com.svalero.apievents.repository;

import com.svalero.apievents.domain.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    // Obtener todos los pagos
    List<Payment> findAll();

    // Buscar pagos por fecha de pago
    List<Payment> findByPaymentDate(LocalDate paymentDate);

    // Buscar pagos entre dos fechas
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    // Buscar pagos por estado (por ejemplo: "PAID", "PENDING")
    List<Payment> findByStatus(String status);

    // Buscar pagos por cantidad mayor o igual a un monto
    List<Payment> findByAmountGreaterThanEqual(double amount);

    // Buscar pagos relacionados a una reserva específica
    List<Payment> findByReservationId(Long reservationId);
}
