package com.example.yetiproject.repository;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
	SseEmitter save(String emitterId, SseEmitter sseEmitter);
	void saveEventCache(String eventCacheId, Object event);
	Map<String, SseEmitter> findAll();
	Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId);
	Map<String, Object> findAllEventCacheStartWithByUserId(String userId);
	void deleteById(String id);
	void deleteAllEmitterStartWithId(String userId);
	void deleteAllEventCacheStartWithId(String userId);
}
