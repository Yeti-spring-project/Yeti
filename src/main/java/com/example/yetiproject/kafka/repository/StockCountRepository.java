package com.example.yetiproject.kafka.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StockCountRepository {
	private final RedisTemplate<String, String> redisTemplate;

	public Long increment(){
		return redisTemplate
			.opsForValue()
			.increment("stock_count");
	}
}
