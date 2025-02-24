package com.CactiEncyclopedia.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "families")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Family extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String imageURL;

    @OneToMany(mappedBy = "family")
    private List<Species> speciesList;
}
