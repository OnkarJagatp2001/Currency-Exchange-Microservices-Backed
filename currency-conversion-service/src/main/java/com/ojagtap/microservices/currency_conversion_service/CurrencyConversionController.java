package com.ojagtap.microservices.currency_conversion_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
class RestTemplateConfiguration {
    @Bean
    @org.springframework.cloud.client.loadbalancer.LoadBalanced
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(@PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity) {
        String URL = "http://currency-exchange-service/currency-exchange/from/{from}/to/{to}"; // logical service name
                                                                                               // provided by Eureka
        // previous hardcoded host/port removed
        Map<String, String> uriVariables = Map.of("from", from, "to", to);

        // Synchronous blocking call to the external service
        /*
         * ResponseEntity<CurrencyConversion> responseFromCurrencyExchange =
         * new RestTemplate().getForEntity(URL, CurrencyConversion.class, uriVariables);
         */

        ResponseEntity<CurrencyConversion> responseFromCurrencyExchange = restTemplate.getForEntity(URL,
                CurrencyConversion.class, uriVariables);

        CurrencyConversion currencyConversion = responseFromCurrencyExchange.getBody();
        assert currencyConversion != null;
        BigDecimal totalCalculatedAmount = quantity.multiply(currencyConversion.getConversionMultiple());
        return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
                currencyConversion.getConversionMultiple(),
                totalCalculatedAmount,
                currencyConversion.getEnvironment());
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity) {

        CurrencyConversion currencyConversion = currencyExchangeProxy.retrieveExchangeValue(from, to);
        assert currencyConversion != null;
        BigDecimal totalCalculatedAmount = quantity.multiply(currencyConversion.getConversionMultiple());
        return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
                currencyConversion.getConversionMultiple(),
                totalCalculatedAmount,
                currencyConversion.getEnvironment() + " " + "feign");
    }

    /**
     * Bulk conversion endpoint accepting a list of requests.
     */
    @PostMapping("/currency-conversion/bulk")
    public java.util.List<CurrencyConversion> bulkConvert(
            @org.springframework.web.bind.annotation.RequestBody java.util.List<ConversionRequest> requests) {
        java.util.List<CurrencyConversion> results = new java.util.ArrayList<>();
        for (ConversionRequest req : requests) {
            results.add(calculateCurrencyConversion(req.getFrom(), req.getTo(), req.getQuantity()));
        }
        return results;
    }

    @GetMapping("/currency-conversion/available-pairs")
    public java.util.List<String> availablePairs() {
        // proxy returns CurrencyConversion but we only care about from/to
        java.util.List<CurrencyConversion> rates = currencyExchangeProxy.getAllRates();
        java.util.List<String> pairs = new java.util.ArrayList<>();
        for (CurrencyConversion r : rates) {
            pairs.add(r.getFrom() + "->" + r.getTo());
        }
        return pairs;
    }

    public static class ConversionRequest {
        private String from;
        private String to;
        private java.math.BigDecimal quantity;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public java.math.BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(java.math.BigDecimal quantity) {
            this.quantity = quantity;
        }
    }

}
