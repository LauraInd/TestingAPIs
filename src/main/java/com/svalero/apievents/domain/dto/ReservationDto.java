package com.svalero.apievents.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private String name;
    private String customerName;
    private String email;
    private LocalDate reservationDate;
    private int quantity;
    private Long eventId;
}
