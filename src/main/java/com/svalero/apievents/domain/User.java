package com.svalero.apievents.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "User")
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "Name is required")
    @Column(nullable = false, unique = true)
    private String name;
    @NotNull(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    private String password;
    @Column(name = "creation_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creationDate;
    private boolean active = true;


}