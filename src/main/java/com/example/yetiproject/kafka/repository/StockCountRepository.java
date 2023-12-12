package com.example.yetiproject.kafka.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StockCountRepository {
	private final RedisTemplate<String, String> redisTemplate;

	public Long increment(){
		return redisTemplate
			.opsForValue()
			.increment("ticket_stock");
	}
}
