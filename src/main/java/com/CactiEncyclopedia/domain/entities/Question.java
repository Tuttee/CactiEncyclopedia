package com.CactiEncyclopedia.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Question extends BaseEntity {
    @Column(nullable = false)
    private String content;

    @ManyToOne
    private User askedBy;

    @ManyToOne
    private Species species;

    @Column(nullable = false)
    private LocalDateTime askedOn;

    @Column
    private boolean approved = false;
}
