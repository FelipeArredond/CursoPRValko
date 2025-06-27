package org.example.handler;

import org.example.model.Transaction;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class LedgerRequestReplyClient {

    private final RabbitTemplate template;

    public LedgerRequestReplyClient(RabbitTemplate template) {
        this.template = template;
    }

    public Mono<Transaction> sendTransaction(Transaction tx) {
        return Mono.fromCallable(() ->
            (Transaction) template.convertSendAndReceive(
                    "ledger.exchange",
                    "ledger.entry.request",
                    tx
            ))
                .doOnNext(transaction -> System.out.println("LEDGER REQUEST REPLY: " + transaction.toString()))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
