package org.example.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    public static final String EXCHANGE = "transaction.exchange";
    public static final String ROUTING_KEY = "transaction.created";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue("transaction.created.queue", true);
    }

    @Bean
    public Binding binding(@Qualifier("queue") Queue queue, @Qualifier("directExchange") DirectExchange ex) {
        return BindingBuilder.bind(queue).to(ex).with(ROUTING_KEY);
    }

    @Bean
    public DirectExchange ledgerExchange() {
        return new DirectExchange("ledger.exchange");
    }

    @Bean
    public Queue ledgerQueue() {
        return new Queue("ledger.entry.request.queue", true);
    }

    @Bean
    public Binding bindingLedger(@Qualifier("ledgerQueue") Queue queue,
                                 @Qualifier("ledgerExchange") DirectExchange ex) {
        return BindingBuilder
                .bind(queue)
                .to(ex)
                .with("ledger.entry.request");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        var template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter());
        template.setReplyTimeout(5000);
        template.setUseTemporaryReplyQueues(true);
        return template;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

}
