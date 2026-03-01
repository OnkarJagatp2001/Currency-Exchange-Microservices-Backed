package com.ojagtap.microservices.currency_exchange_service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Long> {

    // returns the most recently stored rate for a given currency pair
    CurrencyExchange findTopByFromAndToOrderByTimestampDesc(String from, String to);

    // original convenience method retained (could be removed if no longer used)
    CurrencyExchange findByFromAndTo(String from, String to);

    List<CurrencyExchange> findAllByFromAndToOrderByTimestampDesc(String from, String to);
}
