package org.example.service;

import org.example.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class LedgerClient {

    private static final Logger log = LoggerFactory.getLogger(LedgerClient.class);
    private final WebClient client;

    public LedgerClient(@Qualifier("getLedgerClient") WebClient client) {
        this.client = client;
    }

    public Mono<Transaction> postEntry(Transaction tx) {
        log.info("POST /ledger/entries - Request body {}", tx);
        return client.post().uri("/ledger/entries")
                .bodyValue(tx)
                .retrieve()
                .bodyToMono(Transaction.class)
                .doOnNext(res -> log.info("Response for /ledger/entries: {}", res))
                .doOnError(err -> log.error("Error for /ledger/entries", err));
    }

}