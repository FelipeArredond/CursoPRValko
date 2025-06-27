package org.example.handler;

import org.example.dto.TransactionDto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import static org.example.config.AmqpConfig.EXCHANGE;
import static org.example.config.AmqpConfig.ROUTING_KEY;

@Component
public class Publisher {

    private final AmqpTemplate template;

    public Publisher(AmqpTemplate template) {
        this.template = template;
    }

    public void publish(TransactionDto tx) {
        template.convertAndSend(EXCHANGE, ROUTING_KEY, tx);
    }

}
