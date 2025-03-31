package org.example.redis;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Language {
    private String language;
    private Boolean isOfficial;
    private BigDecimal percentage;
}
