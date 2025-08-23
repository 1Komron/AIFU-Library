package aifu.project.libraryweb.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

@Configuration
public class RabbitMQConfig {
    public static final String KEY_EXTEND = "key.extend";
    public static final String QUEUE_EXTEND = "queue.extend";
    public static final String KEY_WARNING = "key.warning";
    public static final String QUEUE_WARNING = "queue.warning";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean(name = "queueExtend")
    public Queue queueExtend() {
        return new Queue(QUEUE_EXTEND, true, false, false);
    }

    @Bean(name = "queueWarning")
    public Queue queue() {
        return new Queue(QUEUE_WARNING, true);
    }

    @Bean
    public Binding extendBinding(@Qualifier("queueExtend") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(KEY_EXTEND);
    }

    @Bean
    public Binding binding2(@Qualifier("queueWarning") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(KEY_WARNING);
    }

    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}