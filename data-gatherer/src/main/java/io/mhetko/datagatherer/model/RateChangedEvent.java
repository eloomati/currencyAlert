package io.mhetko.datagatherer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateChangedEvent {
    private String base;   // typ waluty bazowej
    private String symbol; // typ waluty docelowej
    private double rate;   // nowy kurs
}
