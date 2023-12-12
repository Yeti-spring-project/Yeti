package com.example.yetiproject.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketStockProducer {
	private final KafkaTemplate<String, Long> kafkaTemplate;

	public void stockCreate(Long userId){ kafkaTemplate.send("ticket_stock", userId); }

}
