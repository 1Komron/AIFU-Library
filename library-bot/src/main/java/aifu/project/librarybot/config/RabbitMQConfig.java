package aifu.project.librarybot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String KEY_BORROW = "key.borrow";
    public static final String KEY_EXTEND = "key.extend";
    public static final String KEY_RETURN = "key.return";
    public static final String KEY_REGISTER = "key.register";

    public static final String QUEUE_BORROW = "queue.borrow";
    public static final String QUEUE_EXTEND = "queue.extend";
    public static final String QUEUE_RETURN = "queue.return";
    public static final String QUEUE_REGISTER = "queue.register";

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    @Bean
    public Queue queueBorrow() {
        return new Queue(QUEUE_BORROW, true);
    }

    @Bean
    public Queue queueExtend() {
        return new Queue(QUEUE_EXTEND, true);
    }

    @Bean
    public Queue queueReturn() {
        return new Queue(QUEUE_RETURN, true);
    }

    @Bean
    public Queue queueRegister() {
        return new Queue(QUEUE_REGISTER, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public Binding binding1(Queue queueBorrow, TopicExchange exchange) {
        return BindingBuilder.bind(queueBorrow).to(exchange).with(KEY_BORROW);
    }

    @Bean
    public Binding binding2(Queue queueExtend, TopicExchange exchange) {
        return BindingBuilder.bind(queueExtend).to(exchange).with(KEY_EXTEND);
    }

    @Bean
    public Binding binding3(Queue queueReturn, TopicExchange exchange) {
        return BindingBuilder.bind(queueReturn).to(exchange).with(KEY_RETURN);
    }

    @Bean
    public Binding binding4(Queue queueRegister, TopicExchange exchange) {
        return BindingBuilder.bind(queueRegister).to(exchange).with(KEY_REGISTER);
    }

    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

