package aifu.project.librarybot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Bean(name = "queueBorrow")
    public Queue queueBorrow() {
        return new Queue(QUEUE_BORROW, true);
    }

    @Bean(name = "queueExtend")
    public Queue queueExtend() {
        return new Queue(QUEUE_EXTEND, true);
    }

    @Bean(name = "queueReturn")
    public Queue queueReturn() {
        return new Queue(QUEUE_RETURN, true);
    }

    @Bean(name = "queueRegister")
    public Queue queueRegister() {
        return new Queue(QUEUE_REGISTER, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public Binding binding1(@Qualifier("queueBorrow") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(KEY_BORROW);
    }

    @Bean
    public Binding binding2(@Qualifier("queueExtend") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(KEY_EXTEND);
    }

    @Bean
    public Binding binding3(@Qualifier("queueReturn") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(KEY_RETURN);
    }

    @Bean
    public Binding binding4(@Qualifier("queueRegister") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(KEY_REGISTER);
    }

    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
