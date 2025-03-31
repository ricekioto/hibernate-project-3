package org.example.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "world", name = "country_language")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CountryLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    Country country;

    @Column(nullable = false)
    String language;

    @Column(name = "is_official", columnDefinition = "BIT", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    Boolean isOfficial;

    @Column(nullable = false)
    BigDecimal percentage;
}