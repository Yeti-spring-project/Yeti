package com.example.yetiproject.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.service.TicketServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "ReserveTicketConsumer")
@Component
@RequiredArgsConstructor
public class ReserveTicketConsumer {
	private final ObjectMapper objectMapper;
	private final TicketServiceImpl ticketServiceImpl;
	@KafkaListener(topics = "ticketReserveUser", groupId = "ticket")
	public void consume(ConsumerRecord<String, String> record){
		try{
			TicketRequestDto ticketRequestDto= objectMapper.readValue(record.value(), TicketRequestDto.class);
			User user = User.builder().userId(ticketRequestDto.getUserId()).build();
			ticketServiceImpl.reserveTicket(user, ticketRequestDto);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
