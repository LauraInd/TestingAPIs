package com.svalero.apievents.service;

import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.exception.ReservationNotFoundException;
import com.svalero.apievents.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Obtener todas las reservas
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Obtener reservas por nombre del cliente
    public List<Reservation> getReservationsByCustomerName(String name) {
        return reservationRepository.findByCustomerNameContaining(name);
    }

    // Obtener reservas por fecha específica
    public List<Reservation> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByReservationDate(date);
    }

    // Obtener reservas entre dos fechas
    public List<Reservation> getReservationsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findByReservationDateBetween(startDate, endDate);
    }

    // Obtener reservas por cantidad de entradas
    public List<Reservation> getReservationsByQuantity(int quantity) {
        return reservationRepository.findByQuantity(quantity);
    }

    // Obtener reservas asociadas a un evento
    public List<Reservation> getReservationsByEvent(Long eventId) {
        return reservationRepository.findByEventId(eventId);
    }

    // Guardar una nueva reserva
    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    // Obtener una reserva por ID
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + id));
    }

    // Actualizar una reserva por ID
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + id));

        // Actualizar los campos de la reserva
        existingReservation.setCustomerName(reservationDetails.getCustomerName());
        existingReservation.setEmail(reservationDetails.getEmail());
        existingReservation.setReservationDate(reservationDetails.getReservationDate());
        existingReservation.setQuantity(reservationDetails.getQuantity());
        existingReservation.setEvent(reservationDetails.getEvent());

        return reservationRepository.save(existingReservation);
    }

    public Reservation updateReservationPartial(Long id, Map<String, Object> updates) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Reservation.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, reservation, value);
            }
        });

        return reservationRepository.save(reservation);
    }

    // Eliminar una reserva por ID
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ReservationNotFoundException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }
}
