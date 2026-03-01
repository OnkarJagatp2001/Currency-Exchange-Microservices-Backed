package com.ojagtap.microservices.currency_exchange_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ExchangeRateFetcher {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateFetcher.class);

    @Autowired
    private CurrencyExchangeRepository repository;

    @Value("${fetch.rate.ms:60000}")
    private long fetchRateMs;

    @Value("${exchange.api.key:}")
    private String apiKey;

    @Value("${exchange.api.base-url:https://v6.exchangerate-api.com/v6}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // list of currencies to keep in sync; could be externalized
    private static final String[] CURRENCIES = new String[] { "USD", "INR", "EUR" };

    @Scheduled(fixedRateString = "${fetch.rate.ms:60000}")
    public void scheduledFetch() {
        logger.info("Fetching latest exchange rates from external API");
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("No API key configured; skipping fetch");
            return;
        }
        try {
            // use configured exchangerate-api.com standard request with API key
            String url = String.format("%s/%s/latest/%s", apiBaseUrl, apiKey, "USD");
            ExchangeResponse response = restTemplate.getForObject(url, ExchangeResponse.class);
            if (response != null && response.getRates() != null) {
                for (String to : CURRENCIES) {
                    if ("USD".equals(to))
                        continue;
                    BigDecimal rate = response.getRates().get(to);
                    if (rate != null) {
                        CurrencyExchange entity = new CurrencyExchange();
                        entity.setFrom("USD");
                        entity.setTo(to);
                        entity.setConversionMultiple(rate);
                        entity.setTimestamp(java.time.LocalDateTime.now());
                        repository.save(entity);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error fetching exchange rates", ex);
        }
    }

    /**
     * Fetch a single conversion rate from external API and persist it.
     * Returns the saved entity or null on failure.
     */
    public CurrencyExchange fetchAndSave(String base, String to) {
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("No API key configured; cannot fetch rate");
            return null;
        }
        try {
            String url = String.format("%s/%s/latest/%s", apiBaseUrl, apiKey, base);
            ExchangeResponse response = restTemplate.getForObject(url, ExchangeResponse.class);
            if (response != null) {
                Map<String, BigDecimal> rates = response.getRates() != null ? response.getRates() : response.getConversion_rates();
                if (rates != null && rates.containsKey(to)) {
                    BigDecimal rate = rates.get(to);
                    CurrencyExchange entity = new CurrencyExchange();
                    entity.setFrom(base);
                    entity.setTo(to);
                    entity.setConversionMultiple(rate);
                    entity.setTimestamp(java.time.LocalDateTime.now());
                    return repository.save(entity);
                }
            }
        } catch (Exception ex) {
            logger.error("Error fetching single exchange rate", ex);
        }
        return null;
    }

    // simple inner class to map the JSON response
    public static class ExchangeResponse {
        // Some APIs return 'rates', others return 'conversion_rates'
        private Map<String, BigDecimal> rates;
        private Map<String, BigDecimal> conversion_rates;

        public Map<String, BigDecimal> getRates() { return rates; }
        public void setRates(Map<String, BigDecimal> rates) { this.rates = rates; }

        public Map<String, BigDecimal> getConversion_rates() { return conversion_rates; }
        public void setConversion_rates(Map<String, BigDecimal> conversion_rates) { this.conversion_rates = conversion_rates; }
    }
}