package org.example.service;

import io.netty.handler.timeout.TimeoutException;
import org.example.dto.CashRequestDto;
import org.example.dto.TransactionDto;
import org.example.handler.LedgerRequestReplyClient;
import org.example.handler.Publisher;
import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.example.model.TransactionType;
import org.example.repo.TransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.Instant;

@Service
public class TransactionService {
    private final TransactionRepository repository;
    private final LedgerClient ledgerClient;
    private final LedgerRequestReplyClient ledgerRequestReplyClient;
    private final Publisher publisher;

    public TransactionService(TransactionRepository repository, LedgerClient ledgerClient, LedgerRequestReplyClient ledgerRequestReplyClient, Publisher publisher) {
        this.repository = repository;
        this.ledgerClient = ledgerClient;
        this.ledgerRequestReplyClient = ledgerRequestReplyClient;
        this.publisher = publisher;
    }

    public Mono<TransactionDto> cashIn(CashRequestDto request) {
        Transaction tx = Transaction.builder()
                .amount(request.amount())
                .currency(request.currency())
                .type(TransactionType.CASH_IN)
                .status(TransactionStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        return repository.save(tx)
                .flatMap(transaction -> ledgerClient.postEntry(tx))
                .map(this::toDto)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof TimeoutException)
                        .onRetryExhaustedThrow((retry, sig) -> sig.failure())
                )
                .onErrorResume(throwable -> rollback(tx, throwable))
                .doOnSuccess(publisher::publish);
    }

    public Mono<TransactionDto> cashOt(CashRequestDto request) {
        Transaction tx = Transaction.builder()
                .amount(request.amount())
                .currency(request.currency())
                .type(TransactionType.CASH_OUT)
                .status(TransactionStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        return ledgerRequestReplyClient.sendTransaction(tx)
                .flatMap(repository::save)
                .flatMap(transaction -> ledgerClient.postEntry(tx))
                .map(this::toDto)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof TimeoutException)
                        .onRetryExhaustedThrow((retry, sig) -> sig.failure())
                )
                .onErrorResume(throwable -> rollback(tx, throwable))
                .doOnSuccess(publisher::publish);
    }

    private Mono<TransactionDto> rollback(Transaction tx, Throwable throwable) {
        tx.setStatus(TransactionStatus.FAILED);
        return repository.save(tx).then(Mono.error(throwable));
    }

    private TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt());
    }

}