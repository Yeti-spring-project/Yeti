package com.example.yetiproject.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j(topic = "ticket producer")
public class ReserveTicketProducer {
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public void send(Long userId, TicketRequestDto ticketRequestDto){
		try{
			ticketRequestDto.setUserId(userId);
			String jsonObject = objectMapper.writeValueAsString(ticketRequestDto);
			kafkaTemplate.send("ticketReserveUser", jsonObject);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
