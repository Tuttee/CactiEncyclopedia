package com.CactiEncyclopedia.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Species extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String imageURL;

    @ManyToOne(optional = false)
    private User createdBy;

    @ManyToOne(optional = false)
    private Family family;

    @Column(nullable = false, columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String cultivation;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String coldHardiness;

    @Column
    private LocalDate addedOn;

    @Column
    private boolean approved = false;

    @OneToMany(mappedBy = "species")
    private List<Question> questions;
}
