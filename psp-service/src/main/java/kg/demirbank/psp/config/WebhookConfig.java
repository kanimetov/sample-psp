package kg.demirbank.psp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration for webhook notification system
 * Sets up RabbitMQ exchange, queue, DLQ and RestTemplate for HTTP delivery
 */
@Configuration
@ConditionalOnProperty(name = "webhook.enabled", havingValue = "true", matchIfMissing = true)
public class WebhookConfig {
    
    // RabbitMQ Configuration
    @Bean
    public TopicExchange webhookExchange() {
        return new TopicExchange("webhook.merchant.notify", true, false);
    }
    
    @Bean
    public Queue webhookQueue() {
        return QueueBuilder.durable("webhook.merchant.notify.queue").build();
    }
    
    @Bean
    public Queue webhookDlq() {
        return QueueBuilder.durable("webhook.merchant.notify.dlq").build();
    }
    
    @Bean
    public Binding webhookBinding() {
        return BindingBuilder.bind(webhookQueue())
                .to(webhookExchange())
                .with("merchant.webhook");
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
    
    // RestTemplate Configuration for HTTP calls
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
}

