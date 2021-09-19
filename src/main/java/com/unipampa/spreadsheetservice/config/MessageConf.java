package com.unipampa.spreadsheetservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConf {
  @Value("${rabbitmq.queue}")
  private String queue;
  @Value("${rabbitmq.exchange}")
  private String exchange;
  @Value("${rabbitmq.routingkey}")
  private String routingKey;

  @Bean
  public Binding declareBinding() {
    return BindingBuilder.bind(new Queue(queue)).to(new TopicExchange(exchange)).with(routingKey);
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
