package com.example.yetiproject.redis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.facade.repository.RedisRepository;
import com.example.yetiproject.facade.sortedset.WaitingQueueSortedSetService;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest
public class RedisWaitingSortedSetTest {
	@MockBean
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	WaitingQueueSortedSetService waitingQueueSortedSetService;
	@Autowired
	RedisRepository redisRepository;

	private final String USER_QUEUE_WAIT_KEY = "ticketInfo:queue:1:wait";

	@Test
	@DisplayName("redis sortedset 순서 보장되는지 확인")
	void test1() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L).seat(String.valueOf(i+"A")+i)
				.build();
			long userId = i+1;
			executorService.submit(() ->{
				try {
					Long rank = waitingQueueSortedSetService.registerQueue(userId, ticketRequestDto);
					System.out.println("userId: " + userId + " = " +  rank);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
				finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		long result = redisRepository.zCard(USER_QUEUE_WAIT_KEY);
		assertEquals(100, result);
	}

	@Test
	@DisplayName("waiting sortedset queue에 들어갔다 rank를 반환하는 속도를 계산")
	void test2() throws JsonProcessingException {
		int count = 50000;
		long startTime = System.currentTimeMillis();
		for (int i = 1; i <= count; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L).seat(String.valueOf(i+"A")+i)
				.build();
			long userId = 1;
			waitingQueueSortedSetService.registerQueue(userId, ticketRequestDto);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("%s개 (redis sortedset) 저장 속도 = %s".formatted(count, (endTime - startTime)));
	}
}
