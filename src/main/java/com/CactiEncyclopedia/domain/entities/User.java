package com.CactiEncyclopedia.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseEntity{
    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToOne(optional = false)
    private UserRole role;

    @OneToMany(mappedBy = "createdBy")
    private List<Species> addedSpecies;

    @OneToMany(mappedBy = "askedBy")
    private List<Question> askedQuestions;
}
