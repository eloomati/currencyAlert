package io.mhetko.datagatherer.provider;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.mhetko.datagatherer.config.ProviderProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.mhetko.datagatherer.provider.exception.ProviderAuthException;
import io.mhetko.datagatherer.provider.exception.ProviderMalformedResponseException;
import io.mhetko.datagatherer.provider.exception.ProviderRateLimitException;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenExchangeRatesClientTest {

    private static final String TEST_API_KEY = "test-key";
    static WireMockServer server;
    private static OpenExchangeRatesClient client;

    @BeforeAll
    static void setup() {
        server = new WireMockServer(0); // losowy wolny port
        server.start();
        configureFor("localhost", server.port());

        ProviderProperties properties = new ProviderProperties(
                "openexchangerates",
                "PLN",
                List.of("USD", "EUR"),
                server.baseUrl() + "/latest.json",
                TEST_API_KEY
        );
        client = new OpenExchangeRatesClient(properties);
    }

    @AfterAll
    static void teardown() {
        server.stop();
    }

    @Test
    void latest_shouldReturn200() {
        // given
        stubFor(get(urlPathEqualTo("/latest.json"))
                .withQueryParam("app_id", equalTo(TEST_API_KEY))
                .withQueryParam("base", equalTo("PLN"))
                .withQueryParam("symbols", equalTo("USD,EUR"))
                .willReturn(okJson("""
            {
              "base": "PLN",
              "timestamp": 1710000000,
              "rates": {
                "USD": 4.00,
                "EUR": 4.35,
                "GBP": 5.10
              }
            }
            """)));

        //when
        Map<String, Double> out = client.latest("PLN", List.of("USD", "EUR"));

        //then
        assertEquals(2, out.size());
        assertEquals(4.00, out.get("USD"));
        assertEquals(4.35, out.get("EUR"));

        verify(getRequestedFor(urlPathEqualTo("/latest.json"))
                .withQueryParam("app_id", equalTo(TEST_API_KEY))
                .withQueryParam("base", equalTo("PLN"))
                .withQueryParam("symbols", equalTo("USD,EUR")));
    }

    @Test
    void latest_shouldSkipMissingSymbol(){
        // given
        stubFor(get(urlPathEqualTo("/latest.json"))
                .withQueryParam("app_id", equalTo(TEST_API_KEY))
                .withQueryParam("base", equalTo("PLN"))
                .withQueryParam("symbols", equalTo("USD,EUR"))
                .willReturn(okJson("""
            {
              "base": "PLN",
              "timestamp": 1710000000,
              "rates": {
                "USD": 4.00
              }
            }
            """)));

        //when
        Map<String, Double> out = client.latest("PLN", List.of("USD", "EUR"));

        // then
        assertEquals(1, out.size());
        assertEquals(4.00, out.get("USD"));
        assertFalse(out.containsKey("EUR"));
    }


    @Test
    void latest_shouldThrowAuthException_401() {
        // given
        stubFor(get(urlPathEqualTo("/latest.json"))
                .willReturn(aResponse().withStatus(401)));

        // when + then
        assertThrows(ProviderAuthException.class,
                () -> client.latest("PLN", List.of("USD")));
    }

    @Test
    void latest_shouldThrowAuthException_403() {
        // given
        stubFor(get(urlPathEqualTo("/latest.json"))
                .willReturn(aResponse().withStatus(403)));

        // when + then
        assertThrows(ProviderAuthException.class,
                () -> client.latest("PLN", List.of("USD")));
    }

    @Test
    void latest_shouldThrowRateLimit_on429() {
        // given
        stubFor(get(urlPathEqualTo("/latest.json"))
                .willReturn(aResponse().withStatus(429)));

        // when + then
        assertThrows(ProviderRateLimitException.class,
                () -> client.latest("USD", List.of("PLN")));
    }

    @Test
    void latest_shouldThrowMalformed_whenRatesMissing() {
        // given:
        stubFor(get(urlPathEqualTo("/latest.json"))
                .willReturn(okJson("""
                {
                  "base": "USD",
                  "timestamp": 1710000000
                }
                """)));

        // when + then
        assertThrows(ProviderMalformedResponseException.class,
                () -> client.latest("USD", List.of("PLN")));
    }
}
