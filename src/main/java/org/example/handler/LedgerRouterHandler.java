package org.example.handler;

import org.example.model.TransactionStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class LedgerRouterHandler {

    @Bean
    public RouterFunction<ServerResponse> ledgerRouter() {
        return RouterFunctions.route(POST("/ledger/entries"), this::createEntry);
    }

    private Mono<ServerResponse> createEntry(ServerRequest request) {
        return request.bodyToMono(Map.class)
                .flatMap(body -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("status", TransactionStatus.POSTED,
                                "id", UUID.randomUUID().toString(),
                                "createdAt", Instant.now()
                                )
                        )
                );
    }

}
