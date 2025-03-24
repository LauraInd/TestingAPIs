package com.svalero.apievents.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "EventCategory")
@Table(name = "event_categories")
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "Name  is required")
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String description;
    @NotNull(message = "Date is required")
    @Column(name = "creation_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creationDate;
    @Column(name = "number_events", nullable = false)
    private int numberEvents;
    private Boolean active = true;

    @OneToMany(mappedBy = "category")
    @JsonBackReference(value = "event-category")
    private List<Event> events;

}
