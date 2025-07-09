package org.example.handler;

import org.example.dto.CashRequestDto;
import org.example.model.TransactionStatus;
import org.example.service.TransactionService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class TransactionHandler {
    private final TransactionService service;

    public TransactionHandler(TransactionService service) {
        this.service = service;
    }

    public Mono<ServerResponse> cashIn(ServerRequest request) {
        return request.bodyToMono(CashRequestDto.class)
                .flatMap(service::cashIn)
                .filter(transactionDto -> transactionDto.status().equals(TransactionStatus.POSTED))
                .flatMap(transactionDto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(transactionDto));
    }

    public Mono<ServerResponse> cashOut(ServerRequest request) {
        return request.bodyToMono(CashRequestDto.class)
                .flatMap(service::cashOt)
                .filter(transactionDto -> transactionDto.status().equals(TransactionStatus.POSTED))
                .flatMap(transactionDto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(transactionDto));
    }

    public Mono<ServerResponse> findTransactionById(ServerRequest request) {
        var transactionId = request.pathVariable("id");
        return service.findById(transactionId)
                .flatMap(transactionDto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(transactionDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}