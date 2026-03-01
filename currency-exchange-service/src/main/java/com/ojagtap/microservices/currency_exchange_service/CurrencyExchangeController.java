package com.ojagtap.microservices.currency_exchange_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import java.math.BigDecimal;

@RestController
public class CurrencyExchangeController {
    private Logger logger = LoggerFactory.getLogger(CurrencyExchange.class);

    @Autowired
    private Environment environment;

    @Autowired
    private CurrencyExchangeRepository currencyExchangeRepository;

    @Autowired
    private ExchangeRateFetcher fetcher; // to allow manual refresh

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyExchange retriveExchangeValue(@PathVariable String from, @PathVariable String to) {
        logger.info("retrieveExchangeValue called with {} to {}", from, to);
        CurrencyExchange currencyExchange = currencyExchangeRepository
                .findTopByFromAndToOrderByTimestampDesc(from, to);
        if (currencyExchange == null) {
            logger.info("No cached rate found for {}->{}; attempting live fetch", from, to);
            CurrencyExchange fetched = fetcher.fetchAndSave(from, to);
            if (fetched != null) {
                currencyExchange = fetched;
            } else {
                throw new RuntimeException("Unable to find data for " + from + " to " + to);
            }
        }

        String port = environment.getProperty("local.server.port");
        currencyExchange.setEnvironment(port);

        return currencyExchange;
    }

    @GetMapping("/currency-exchange/all")
    public List<CurrencyExchange> retrieveAll() {
        return currencyExchangeRepository.findAll();
    }

    @GetMapping("/currency-exchange/history/from/{from}/to/{to}")
    public List<CurrencyExchange> history(@PathVariable String from, @PathVariable String to) {
        return currencyExchangeRepository.findAllByFromAndToOrderByTimestampDesc(from, to);
    }

    /**
     * Manual refresh endpoint, triggers the scheduled fetch immediately.
     */
    @PostMapping("/currency-exchange/refresh")
    public String refreshRates() {
        fetcher.scheduledFetch();
        return "Refresh triggered";
    }
}
