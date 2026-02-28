package com.ojagtap.api_gateway;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        Function<PredicateSpec, Buildable<Route>> routeFunction
                = p -> p.path("/get")
                .filters(f ->
                        f.addRequestHeader("API_GATEWAY_HEADER", "MyURL")
                                .addRequestParameter("Param", "MyParamValue"))
                .uri("http://httpbin.org:80");

        return builder.routes().route(routeFunction)
                .route(p -> p.path("/currency-exchange/**") // URL used path
                        .uri("lb://currency-exchange-service")) // actual service path
                .route(p -> p.path("/currency-conversion/**") // URL used path
                        .uri("lb://currency-conversion-service")) // actual service path
                .route(p -> p.path("/currency-conversion-feign/**") // URL used path
                        .uri("lb://currency-conversion-service")) // actual service path
                .route(p -> p.path("/currency-conversion-new/**") // URL used path
                        .filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)","/currency-conversion-feign/${segment}"))
                        .uri("lb://currency-conversion-service")) // actual service path
                .build();
    }
}
