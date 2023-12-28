package com.example.yetiproject.kafka.service;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.kafka.producer.ReserveTicketProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "TicketKafkaService")
@Transactional
@RequiredArgsConstructor
public class TicketKafkaService {
	private final ReserveTicketProducer reserveTicketProducer;

	public void sendReserveTicket(Long userId, TicketRequestDto ticketRequestDto){
		log.info("Producer 데이터 보냄");
		reserveTicketProducer.send(userId, ticketRequestDto);
	}
}
