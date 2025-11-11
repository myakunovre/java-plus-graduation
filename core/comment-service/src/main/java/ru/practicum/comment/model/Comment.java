package ru.practicum.comment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import interaction.model.event.State;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String text;
    Long eventId;
    Long authorId;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "modified_on")
    LocalDateTime modifiedOn;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    State state;
}