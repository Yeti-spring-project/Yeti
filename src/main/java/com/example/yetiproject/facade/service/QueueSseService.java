package com.example.yetiproject.facade.service;

import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.entity.Ticket;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.repository.EmitterRepository;
import com.example.yetiproject.repository.EmitterRepositoryImpl;
import com.example.yetiproject.repository.UserRepository;
import com.example.yetiproject.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "대기열 SSE 통신")
public class QueueSseService {

	private final NotificationService notificationService;
	private final UserRepository userRepository;
	private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Transactional
	public void sortedSetQueueStatus(Long rank, String ticketRequest) throws JsonProcessingException {
		log.info("SSE 통신 메서드 진입");
		TicketRequestDto ticketRequestDto = objectMapper.readValue(ticketRequest, TicketRequestDto.class);
		User user = userRepository.findById(ticketRequestDto.getUserId()).orElse(null);
		log.info("화면에 표시되는 대기열 갱신");
		String content = new String();

		if (rank > 100) {
			content = "접속대기 중입니다. " + rank + "명의 대기자가 있습니다.";
			log.info("대기열 찍어줌");
			notificationService.send(content, user);
		} else {
			content = "예매에 성공했습니다.";
			notificationService.send(content, user);
			String userId = String.valueOf(user.getUserId());
			Map<String, SseEmitter> emitters = emitterRepository.findAll();
			System.out.println("emitters = " + emitters);
			// emitters.values().forEach(SseEmitter::complete);
			// emitterRepository.deleteAllEmitterStartWithId(userId);
		}
	}
}
