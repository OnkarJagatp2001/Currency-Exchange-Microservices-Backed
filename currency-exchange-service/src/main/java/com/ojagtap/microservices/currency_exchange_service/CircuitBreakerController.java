package com.ojagtap.microservices.currency_exchange_service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {
    private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    @GetMapping("/sample-api")
    //@Retry(name="sample-api", fallbackMethod = "fallbackResponse")
    //@CircuitBreaker(name="default", fallbackMethod = "fallbackResponse")
    //@RateLimiter(name="default") // In 10 sec only 10K request will get fired
    //@Bulkhead(name="default")
    @Bulkhead(name = "default", fallbackMethod = "fallbackResponse")
    public String sampleApi() throws InterruptedException {
        logger.info("Sample API call received");
        Thread.sleep(3000); // Simulate a slow service
        return "✅ Successful call";
        /*
        ResponseEntity<String> forEntity =
        new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", String.class);
        return forEntity.getBody();
        */
    }

    public String fallbackResponse(Exception ex) {
        logger.error("Fallback executed: {}", ex.getMessage());
        return "Fallback response";
    }
}
