package com.example.yetiproject.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.yetiproject.dto.notification.NotificationResponseDto;
import com.example.yetiproject.entity.Notification;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.repository.EmitterRepository;
import com.example.yetiproject.repository.EmitterRepositoryImpl;
import com.example.yetiproject.repository.NotificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "알림 서비스 접근")
public class NotificationService {

	private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
	private final NotificationRepository notificationRepository;
	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

	@Transactional
	public SseEmitter subscribe(Long userId, String lastEventId) {

		// emitter와 event의 순서를 구별하기 위해 userId에 시간 추가
		String emitterId = userId + "_" + System.currentTimeMillis();
		SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
		emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
		emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

		// 503 에러를 방지하기 위한 더미 이벤트
		String eventId = userId + "_" + System.currentTimeMillis();
		sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

		// 클라이언트가 수신하지 못한 이벤트가 존재하는 경우 전송
		// if (!lastEventId.isEmpty()) {
		// 	Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
		// 	eventCaches.entrySet().stream()
		// 		.filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
		// 		.forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
		// }

		return emitter;
	}

	public void send(String content, User receiver) {

		// Test용 예외처리
		if (receiver == null) {
			return ;
		}

		Notification notification = notificationRepository.save(new Notification(content, receiver));

		String receiverId = String.valueOf(receiver.getUserId());
		String eventId = receiverId + "_" + System.currentTimeMillis();
		Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);
		emitters.forEach(
			(key, emitter) -> {
				emitterRepository.saveEventCache(key, notification);
				sendNotification(emitter, eventId, key, new NotificationResponseDto(notification));
			}
		);
	}

	public void complete(User receiver) {

		// Test용 예외처리
		if (receiver == null) {
			return ;
		}

		String receiverId = String.valueOf(receiver.getUserId());

		Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);
		emitters.forEach(
			(key, emitter) -> {
				emitter.complete();
			}
		);
	}

	public Map<String, SseEmitter> getEmitterList() {
		return emitterRepository.findAll();
	}

	private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
		try {
			// Emitter로 event를 전송
			emitter.send(SseEmitter.event()
				.id(eventId)
				.data(data));
		} catch (IOException e) {
			// 에러 발생시 Emitter 삭제
			emitterRepository.deleteById(emitterId);
		}
	}
}
