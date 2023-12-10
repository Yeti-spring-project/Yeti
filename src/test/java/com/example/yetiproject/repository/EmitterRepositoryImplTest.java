package com.example.yetiproject.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.yetiproject.entity.Notification;
import com.example.yetiproject.entity.User;

class EmitterRepositoryImplTest {

	private EmitterRepository emitterRepository = new EmitterRepositoryImpl();
	private Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;

	@Test
	@DisplayName("새로운 Emitter 추가")
	public void save() throws Exception {
		//given
		Long userId = 1L;
		String emitterId = userId + "_" + System.currentTimeMillis();
		SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

		//when, then
		System.out.println("emitterId = " + emitterId);
		Assertions.assertDoesNotThrow(() -> emitterRepository.save(emitterId, sseEmitter));
	}

	@Test
	@DisplayName("수신한 이벤트를 캐시에 저장")
	public void saveEventCache() throws Exception {
		//given
		Long userId = 1L;
		String eventCacheId = userId + "_" + System.currentTimeMillis();
		User user = new User();
		user.setUserId(userId);
		Notification notification = new Notification("알림 도착", user);

		//when, then
		Assertions.assertDoesNotThrow(() -> emitterRepository.saveEventCache(eventCacheId, notification));
	}

	@Test
	@DisplayName("특정 회원이 접속한 모든 Emitter 찾기")
	public void findAllEmitterStartWithByMemberId() throws Exception {
		//given
		Long userId = 1L;
		String emitterId1 = userId + "_" + System.currentTimeMillis();
		emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

		Thread.sleep(100);
		String emitterId2 = userId + "_" + System.currentTimeMillis();
		emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

		Thread.sleep(100);
		String emitterId3 = userId + "_" + System.currentTimeMillis();
		emitterRepository.save(emitterId3, new SseEmitter(DEFAULT_TIMEOUT));

		//when
		Map<String, SseEmitter> ActualResult = emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(userId));

		//then
		Assertions.assertEquals(3, ActualResult.size());
	}

	@Test
	@DisplayName("특정 회원에게 수신된 이벤트를 캐시에서 찾기")
	public void findAllEventCacheStartWithByMemberId() throws Exception {
		//given
		Long userId = 1L;
		String eventCacheId1 = userId + "_" + System.currentTimeMillis();
		User user = new User();
		user.setUserId(userId);
		Notification notification1 = new Notification("알림 도착", user);
		emitterRepository.saveEventCache(eventCacheId1, notification1);

		Thread.sleep(100);
		String eventCacheId2 = userId + "_" + System.currentTimeMillis();
		Notification notification2 = new Notification("알림 승인", user);
		emitterRepository.saveEventCache(eventCacheId2, notification2);

		Thread.sleep(100);
		String eventCacheId3 = userId + "_" + System.currentTimeMillis();
		Notification notification3 = new Notification("알림 거부", user);
		emitterRepository.saveEventCache(eventCacheId3, notification3);

		//when
		Map<String, Object> ActualResult = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));

		//then
		Assertions.assertEquals(3, ActualResult.size());
	}

	@Test
	@DisplayName("ID로 Emitter를 Repository에서 제거")
	public void deleteById() throws Exception {
		//given
		Long userId = 1L;
		String emitterId =  userId + "_" + System.currentTimeMillis();
		SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

		//when
		emitterRepository.save(emitterId, sseEmitter);
		emitterRepository.deleteById(emitterId);

		//then
		assertEquals(0, emitterRepository.findAllEmitterStartWithByUserId(emitterId).size());
	}

	@Test
	@DisplayName("저장된 모든 Emitter를 제거")
	public void deleteAllEmitterStartWithId() throws Exception {
		//given
		Long userId = 1L;
		String emitterId1 = userId + "_" + System.currentTimeMillis();
		emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

		Thread.sleep(100);
		String emitterId2 = userId + "_" + System.currentTimeMillis();
		emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

		//when
		emitterRepository.deleteAllEmitterStartWithId(String.valueOf(userId));

		//then
		assertEquals(0, emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(userId)).size());
	}

	@Test
	@DisplayName("캐시에 저장된 모든 이벤트를 제거")
	public void deleteAllEventCacheStartWithId() throws Exception {
		//given
		Long userId = 1L;
		User user = new User();
		user.setUserId(userId);

		String eventCacheId1 =  userId + "_" + System.currentTimeMillis();
		Notification notification1 = new Notification("알림 도착", user);
		emitterRepository.saveEventCache(eventCacheId1, notification1);

		Thread.sleep(100);
		String eventCacheId2 =  userId + "_" + System.currentTimeMillis();
		Notification notification2 = new Notification("알림 승인", user);
		emitterRepository.saveEventCache(eventCacheId2, notification2);

		//when
		emitterRepository.deleteAllEventCacheStartWithId(String.valueOf(userId));

		//then
		assertEquals(0, emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId)).size());
	}
}