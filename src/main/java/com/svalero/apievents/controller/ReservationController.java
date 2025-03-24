package com.svalero.apievents.controller;

import com.svalero.apievents.domain.Reservation;
import com.svalero.apievents.exception.ReservationNotFoundException;
import com.svalero.apievents.service.ReservationService;
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
@RequestMapping("/reservations")
public class ReservationController {

    private final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        logger.info("BEGIN getAllReservations");
        List<Reservation> reservations = reservationService.getAllReservations();
        logger.info("END getAllReservations - Total reservations fetched: {}", reservations.size());
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    // Agregar una nueva reserva
    @PostMapping
    public ResponseEntity<Reservation> addReservation(@RequestBody Reservation reservation) {
        logger.info("BEGIN addReservation - Adding reservation for customer: {}", reservation.getCustomerName());
        Reservation newReservation = reservationService.saveReservation(reservation);
        logger.info("END addReservation - Reservation added with ID: {}", newReservation.getId());
        return new ResponseEntity<>(newReservation, HttpStatus.CREATED);
    }

    // Buscar reservas por nombre del cliente
    @GetMapping("/customer")
    public ResponseEntity<List<Reservation>> getReservationsByCustomerName(@RequestParam String name) {
        logger.info("BEGIN getReservationsByCustomerName - Searching reservations for customer: {}", name);
        List<Reservation> reservations = reservationService.getReservationsByCustomerName(name);
        logger.info("END getReservationsByCustomerName - Total reservations found: {}", reservations.size());
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    // Buscar reservas por fecha de reserva
    @GetMapping("/date")
    public ResponseEntity<List<Reservation>> getReservationsByDate(@RequestParam LocalDate date) {
        logger.info("BEGIN getReservationsByDate - Searching reservations for date: {}", date);
        List<Reservation> reservations = reservationService.getReservationsByDate(date);
        logger.info("END getReservationsByDate - Total reservations found: {}", reservations.size());
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    // Buscar reservas entre dos fechas
    @GetMapping("/range")
    public ResponseEntity<List<Reservation>> getReservationsBetweenDates(
            @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        logger.info("BEGIN getReservationsBetweenDates - Searching reservations between {} and {}", startDate, endDate);
        List<Reservation> reservations = reservationService.getReservationsBetweenDates(startDate, endDate);
        logger.info("END getReservationsBetweenDates - Total reservations found: {}", reservations.size());
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    // Buscar reservas por cantidad de entradas
    @GetMapping("/quantity")
    public ResponseEntity<List<Reservation>> getReservationsByQuantity(@RequestParam int quantity) {
        logger.info("BEGIN getReservationsByQuantity - Searching reservations with quantity: {}", quantity);
        List<Reservation> reservations = reservationService.getReservationsByQuantity(quantity);
        logger.info("END getReservationsByQuantity - Total reservations found: {}", reservations.size());
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    // Buscar reservas asociadas a un evento específico
    @GetMapping("/event")
    public ResponseEntity<List<Reservation>> getReservationsByEvent(@RequestParam Long eventId) {
        logger.info("BEGIN getReservationsByEvent - Searching reservations for event ID: {}", eventId);
        List<Reservation> reservations = reservationService.getReservationsByEvent(eventId);
        logger.info("END getReservationsByEvent - Total reservations found: {}", reservations.size());
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    // Obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        logger.info("BEGIN getReservationById - Fetching reservation with ID: {}", id);
        try {
            Reservation reservation = reservationService.getReservationById(id);
            logger.info("END getReservationById - Reservation found: {}", reservation.getId());
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getReservationById - Reservation not found with ID: {}", id, e);
            throw e;
        }
    }

    // Actualizar una reserva por ID
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservationDetails) {
        logger.info("BEGIN updateReservation - Updating reservation with ID: {}", id);
        try {
            Reservation updatedReservation = reservationService.updateReservation(id, reservationDetails);
            logger.info("END updateReservation - Reservation updated with ID: {}", updatedReservation.getId());
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in updateReservation - Reservation not found with ID: {}", id, e);
            throw e;
        }
    }

    // Actualizar parcialmente una reserva por ID
    @PatchMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("BEGIN updateReservationPartial - Partially updating reservation with ID: {}", id);
        try {
            Reservation updatedReservation = reservationService.updateReservationPartial(id, updates);
            logger.info("END updateReservationPartial - Reservation updated with ID: {}", updatedReservation.getId());
            return ResponseEntity.ok(updatedReservation);
        } catch (Exception e) {
            logger.error("Error in updateReservationPartial - Reservation not found with ID: {}", id, e);
            throw e;
        }
    }

    // Eliminar una reserva por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        logger.info("BEGIN deleteReservation - Deleting reservation with ID: {}", id);
        try {
            reservationService.deleteReservation(id);
            logger.info("END deleteReservation - Reservation deleted with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error in deleteReservation - Reservation not found with ID: {}", id, e);
            throw e;
        }
    }

    // Manejar excepciones de recurso no encontrado
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ReservationNotFoundException exception) {
        logger.error("Handling ReservationNotFoundException - {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}

