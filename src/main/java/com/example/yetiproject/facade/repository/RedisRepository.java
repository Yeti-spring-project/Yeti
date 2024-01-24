package com.example.yetiproject.facade.repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuples;

@Repository
@RequiredArgsConstructor
@Slf4j(topic = "RedisRepository")
public class RedisRepository {
	private final RedisTemplate<String, String> redisTemplate;

	public Boolean zAdd(String key, String value, double score){
		return redisTemplate.opsForZSet().add(key, value, score);
	}
	public Boolean zAddIfAbsent(String key, String value, double score){
		//registQueue에 사용
		return redisTemplate.opsForZSet().addIfAbsent(key, value, score);
	}

	public String get(String key){
		return redisTemplate.opsForValue().get(key);
	}

	public void set(String key, String value){
		redisTemplate.opsForValue().set(key, value);
	}

	public void delete(String key){
		redisTemplate.delete(key);
	}

	public Set<String> zRange(String key, Long start, Long end){

		return redisTemplate.opsForZSet().range(key, start, end);
	}

	public Set<ZSetOperations.TypedTuple<String>> zRangeWithScores(String key, Long start, Long end){
		return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
	}

	public Long zRank(String key, String jsonObject){
		return redisTemplate.opsForZSet().rank(key, jsonObject);
	}

	public Long zRemove(String key, String jsonObject){
		return redisTemplate.opsForZSet().remove(key, jsonObject);
	}

	public Long zSize(String key){
		return redisTemplate.opsForZSet().size(key);
	}

	public Long zCard(String key){
		return redisTemplate.opsForZSet().size(key);
	}

	public Double zScore(String key, String member){
		return redisTemplate.opsForZSet().score(key, member);
	}

	public Long decrease(String key){
		return redisTemplate
			.opsForValue()
			.decrement(key);
	}

	public Long increase(String key) {
		return redisTemplate.opsForValue().increment(key);
	}


	public Long listRightPush(String key, String value){
		return redisTemplate.opsForList().rightPush(key, value);
	}
	public List<String> listRange(String key, long start, long end) {
		return redisTemplate.opsForList().range(key, start, end);
	}

	public Long indexOfRank(String key, String value) {
		return redisTemplate.opsForList().indexOf(key, value);
	}

	public String listLeftPop(String key) {
		return redisTemplate.opsForList().leftPop(key);
	}

	public Long llen(String key){
		return redisTemplate.opsForList().size(key);
	}

	public Boolean hashSetNx(String key, String field, Long userId){
		// 값이 없을때만 값을 설정
		return redisTemplate.opsForHash().putIfAbsent(key, field, userId.toString());
	}
	public Set<ZSetOperations.TypedTuple<String>> popMin(String key, Long count){
		return redisTemplate.opsForZSet().popMin(key, count);
	}

}
