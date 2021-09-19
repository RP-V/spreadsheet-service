package com.unipampa.spreadsheetservice.sender;

import com.unipampa.spreadsheetservice.model.Amostra;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AmostraSender {
  @Value("${rabbitmq.exchange}")
  String exchange;

  @Value("${rabbitmq.routingKey}")
  String routingkey;

  public void sendMessage(RabbitTemplate template, Amostra amostra) {
    template.convertAndSend(exchange, routingkey, amostra);
  }
}
