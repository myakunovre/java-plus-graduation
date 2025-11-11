package ru.practicum.request.model;

import interaction.model.request.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long eventId;
    Long requesterId;
    @Column
    @Enumerated(EnumType.STRING)
    Status status;
    @Column
    LocalDateTime created;
}
