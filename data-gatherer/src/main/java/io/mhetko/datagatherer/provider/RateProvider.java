package io.mhetko.datagatherer.provider;

import java.util.Map;


public interface RateProvider {
    /** Mapa symbol->kurs dla bazy i listy symboli. */
    Map<String, Double> latest(String base, Iterable<String> symbols);
}
