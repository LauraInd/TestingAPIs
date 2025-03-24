package com.svalero.apievents.repository;

import com.svalero.apievents.domain.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    // Método para obtener todas las reservas
    List<Reservation> findAll();

    // Método para buscar reservas por nombre del cliente
    List<Reservation> findByCustomerNameContaining(String customerName);

    // Método para buscar reservas por fecha de reserva exacta
    List<Reservation> findByReservationDate(LocalDate reservationDate);

    // Método para buscar reservas entre dos fechas
    List<Reservation> findByReservationDateBetween(LocalDate startDate, LocalDate endDate);

    // Método para buscar reservas por cantidad exacta de entradas
    List<Reservation> findByQuantity(int quantity);

    // Método para buscar reservas asociadas a un evento específico
    List<Reservation> findByEventId(Long eventId);
}
