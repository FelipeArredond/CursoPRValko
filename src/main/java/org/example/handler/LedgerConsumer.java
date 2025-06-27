package org.example.handler;

import org.example.model.Transaction;
import org.example.model.TransactionStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LedgerConsumer {

    @RabbitListener(queues = "ledger.entry.request.queue")
    public Transaction receive(Transaction tx) {
        System.out.println("LEDGER CLIENT CONSUMER: " + tx.toString());
        tx.setStatus(TransactionStatus.POSTED);
        tx.setCreatedAt(Instant.now());
        return tx;
    }

}
