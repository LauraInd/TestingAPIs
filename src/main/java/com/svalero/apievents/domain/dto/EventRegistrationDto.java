package com.svalero.apievents.domain.dto;


import com.svalero.apievents.domain.EventCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationDto {

    @NotNull(message = "El campo name es obligatorio")
    private String eventName;
    private LocalDate eventDate;
    @Min(value = 1)
    private int capacity;
    private String ubication;
    //private EventCategory category;
    private double latitude;
    private double longitude;
    private long categoryId;

}
