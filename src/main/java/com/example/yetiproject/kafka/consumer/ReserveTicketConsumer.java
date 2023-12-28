package com.example.yetiproject.kafka.consumer;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j(topic = "ReserveTicketConsumer")
@Component
@RequiredArgsConstructor
public class ReserveTicketConsumer {
    private final ObjectMapper objectMapper;
    private final TicketService ticketService;
    @KafkaListener(topics = "ticketReserve", groupId = "ticket")
    public void consume(ConsumerRecord<String, String> record){
        try{
            TicketRequestDto ticketRequestDto= objectMapper.readValue(record.value(), TicketRequestDto.class);
            User user = User.builder().userId(ticketRequestDto.getUserId()).build();
            log.info("객체 생성");
            ticketService.reserveTicket(user, ticketRequestDto);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
