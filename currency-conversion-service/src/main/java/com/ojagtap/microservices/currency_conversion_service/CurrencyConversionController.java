package com.ojagtap.microservices.currency_conversion_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@Configuration(proxyBeanMethods = false)
class RestTemplateConfiguration {
    @Bean
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
                                                          @PathVariable BigDecimal quantity){
        String URL = "http://currency-exchange:8000/currency-exchange/from/USD/to/INR";
        // String URL = "http://localhost:8000/currency-exchange/from/{from}/to/{to}";
        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        // Synchronous blocking call to the external service
        /*
        ResponseEntity<CurrencyConversion> responseFromCurrencyExchange =
            new RestTemplate().getForEntity(URL, CurrencyConversion.class, uriVariables);
        */

        ResponseEntity<CurrencyConversion> responseFromCurrencyExchange =
                restTemplate.getForEntity(URL, CurrencyConversion.class, uriVariables);

        CurrencyConversion currencyConversion = responseFromCurrencyExchange.getBody();
        assert currencyConversion != null;
        BigDecimal totalCalculatedAmount = quantity.multiply(currencyConversion.getConversionMultiple());
        return new CurrencyConversion(currencyConversion.getId()
                , from, to, quantity, currencyConversion.getConversionMultiple(),
                totalCalculatedAmount,
                currencyConversion.getEnvironment());
    }


    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,
                                                          @PathVariable String to,
                                                          @PathVariable BigDecimal quantity){

        CurrencyConversion currencyConversion = currencyExchangeProxy.retrieveExchangeValue(from, to);
        assert currencyConversion != null;
        BigDecimal totalCalculatedAmount = quantity.multiply(currencyConversion.getConversionMultiple());
        return new CurrencyConversion(currencyConversion.getId()
                , from, to, quantity, currencyConversion.getConversionMultiple(),
                totalCalculatedAmount,
                currencyConversion.getEnvironment() + " " + "feign");
    }

}
