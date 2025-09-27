package io.mhetko.datagatherer.provider;

import io.mhetko.datagatherer.config.ProviderProperties;
import io.mhetko.datagatherer.provider.dto.OerLatestResponse;
import io.mhetko.datagatherer.provider.exception.ProviderAuthException;
import io.mhetko.datagatherer.provider.exception.ProviderMalformedResponseException;
import io.mhetko.datagatherer.provider.exception.ProviderRateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Component
@Slf4j
public class OpenExchangeRatesClient implements RateProvider {

    private final RestClient restClient;
    private final ProviderProperties properties;

    public OpenExchangeRatesClient(ProviderProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.apiUrl())
                .build();
        this.properties = properties;
    }

    @Override
    public Map<String, Double> latest(String base, Iterable<String> symbols) {
        List<String> symbolList = new ArrayList<>();
        symbols.forEach(symbolList::add);

        String uri = buildLatestUri(base, symbolList);
        OerLatestResponse dto = fetchLatestDto(uri);
        return extractRequestedRates(dto, symbolList);
    }

    private String buildLatestUri(String base, List<String> symbols) {
        String symbolsParam = String.join(",", symbols);

        return UriComponentsBuilder.fromPath("")
                .queryParam("app_id", properties.apiKey())
                .queryParam("base", base)
                .queryParam("symbols", symbolsParam)
                .build(true)
                .toUriString();
    }

    private OerLatestResponse fetchLatestDto(String uri) {
        OerLatestResponse dto = restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(s -> s.value() == 401 || s.value() == 403,
                        (req, res) -> { throw new ProviderAuthException("Auth error from provider"); })
                .onStatus(s -> s.value() == 429,
                        (req, res) -> { throw new ProviderRateLimitException("Rate-limited by provider"); })
                .body(OerLatestResponse.class);

        if (dto == null || dto.rates() == null) {
            throw new ProviderMalformedResponseException("Missing 'rates' in provider response");
        }
        return dto;
    }

    private Map<String, Double> extractRequestedRates(OerLatestResponse dto, List<String> symbols) {
        Map<String, Double> out = new HashMap<>();
        Map<String, Double> rates = dto.rates();

        for (String symbol : symbols) {
            Double rate = rates.get(symbol);
            if (rate != null) {
                out.put(symbol, rate);
            } else {
                log.warn("No rate for symbol {} in provider response", symbol);
            }
        }
        return out;
    }
}