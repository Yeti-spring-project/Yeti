package com.example.yetiproject.facade.aspect;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.yetiproject.entity.User;
import com.example.yetiproject.repository.UserRepository;
import com.example.yetiproject.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "RankQueueSse")
public class RankQueueSse {

	private final ObjectMapper objectMapper;
	private final NotificationService notificationService;
	private final UserRepository userRepository;

	public void sendMessage(Long userId, Long rank) {
		User user = userRepository.findById(userId).orElse(null);
		String content = "Queue Number: " + rank;
		notificationService.send(content, user);
	}

	public void completeSse(Long userId) {
		User user = userRepository.findById(userId).orElse(null);
		notificationService.complete(user);
	}

	public void printEmitterList() {
		Map<String, SseEmitter> emitters = notificationService.getEmitterList();
		List<String> emitterList = emitters.keySet().stream().toList();
		for (String key : emitterList) {
			log.info("Emitter List: {}", key);
		}
	}
}
