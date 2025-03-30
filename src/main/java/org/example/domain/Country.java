package org.example.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "world", name = "country")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 3, nullable = false)
    String code;

    @Column(name = "code_2", length = 2, nullable = false)
    String code2;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    Continent continent;

    @Column(length = 26, nullable = false)
    String region;

    @Column(name = "surface_area", nullable = false)
    BigDecimal surfaceArea;

    @Column(name = "indep_year")
    Short indepYear;

    @Column(nullable = false)
    Integer population;

    @Column(name = "life_expectancy")
    BigDecimal lifeExpectancy;

    BigDecimal gnp;

    @Column(name = "gnpo_id")
    BigDecimal gnpoId;

    @Column(name = "local_name", length = 45, nullable = false)
    String localName;

    @Column(name = "government_form", length = 45, nullable = false)
    String governmentForm;

    @Column(name = "head_of_state", length = 60)
    String headOfState;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "capital")
    City city;

    @ToString.Exclude
    @OneToMany(mappedBy = "country")
    List<City> cities;

    @OneToMany(mappedBy = "country")
    List<CountryLanguage> countryLanguages;

}
