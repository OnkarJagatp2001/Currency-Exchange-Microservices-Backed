package com.ojagtap.microservices.currency_conversion_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//Spring, in combination with libraries like Spring Cloud OpenFeign,
// uses a proxying mechanism to turn your interface into a functional HTTP client.

//@FeignClient(name="currency-exchange-service", url = "localhost:8000")
@FeignClient(name = "currency-exchange-service")
public interface CurrencyExchangeProxy {
    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyConversion retrieveExchangeValue(@PathVariable String from, @PathVariable String to);

    @GetMapping("/currency-exchange/all")
    public java.util.List<CurrencyConversion> getAllRates();

}
