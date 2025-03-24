package com.svalero.apievents.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Event")
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "Event name is required")
    @Column(name = "event_name",nullable = false, unique = true)
    private String eventName;
    @Column
    private String description;
    @Column(name = "event_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    @Column(nullable = false)
    private int capacity;
    @NotNull(message = "Ubication is required")
    @Column(name = "ubication",nullable = false)
    private String ubication;
    @ColumnDefault("0")
    @Column
    private double latitude;
    @ColumnDefault("0")
    @Column
    private double longitude;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private EventCategory category;
}
