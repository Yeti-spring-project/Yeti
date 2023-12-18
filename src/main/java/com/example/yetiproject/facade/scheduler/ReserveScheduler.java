package com.example.yetiproject.facade.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.facade.service.WaitingQueueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "ReserveScheduler")
@Component
@RequiredArgsConstructor
public class ReserveScheduler {
	private final WaitingQueueService waitingQueueService;
	private final ObjectMapper objectMapper;

	@Scheduled(fixedDelay = 1000)
	public void ticketReserveScheduler(User user, TicketRequestDto requestDto) throws JsonProcessingException {
		// if(reserveService.validEnd()){
		// 	log.info("======= 매진되었습니다. ==========");
		// 	return;
		// }
		String jsonObject = objectMapper.writeValueAsString(requestDto);
		waitingQueueService.publish(String.valueOf(user.getUserId()), requestDto);

	}
}
