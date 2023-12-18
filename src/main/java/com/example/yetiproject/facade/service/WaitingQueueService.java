package com.example.yetiproject.facade.service;

import java.util.Objects;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingQueueService {
	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;
	private static final long FIRST_ELEMENT = 0;
	private static final long LAST_ELEMENT = -1;
	private static final long PUBLISH_SIZE = 100;
	private static final long LAST_INDEX = 1;
	public Boolean registerQueue(Long userId, TicketRequestDto ticketRequestDto) throws JsonProcessingException {
		//객체 -> String 변형
		String jsonObject = objectMapper.writeValueAsString(ticketRequestDto);
		final long now = System.currentTimeMillis();
		log.info("대기열에 추가 - userId : {} requestDto : {} ({}초)", userId, jsonObject, now);
		return redisTemplate.opsForZSet().add( String.valueOf(userId), jsonObject, (int) now);
	}

	public void publish(String userId, TicketRequestDto requestDto){
		final long start = FIRST_ELEMENT;
		final long end = PUBLISH_SIZE - LAST_INDEX;

		Set<String> queue = redisTemplate.opsForZSet().range(requestDto.toString(), start, end);
		for(Object ticketRequest : queue){
			log.info("'{}'님 좌석 예매가 완료되었습니다.", userId);
			redisTemplate.opsForZSet().remove(requestDto.toString(), ticketRequest);
		}

	}
}
