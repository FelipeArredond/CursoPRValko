package org.example.config;

import org.example.handler.TransactionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routerHandler(TransactionHandler handler) {
        return RouterFunctions
                .route()
                .POST("/cash-in", handler::cashIn)
                .POST("/cash-out", handler::cashOut)
                .build();
    }
}