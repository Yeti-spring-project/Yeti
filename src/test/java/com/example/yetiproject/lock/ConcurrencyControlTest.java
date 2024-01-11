package com.example.yetiproject.lock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.facade.RedissonLockTicketFacade;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;
import com.example.yetiproject.service.TicketService;

@WithMockUser
@SpringBootTest
public class ConcurrencyControlTest {
	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private TicketService ticketService;
	@Autowired
	private TicketInfoRepository ticketInfoRepository;
	@Autowired
	private RedissonLockTicketFacade redissonLockTicketFacade;

	@Test
	@DisplayName("일반 ticketservice 100명 예약 테스트/ 비관적 락 / 낙관적락")
	public void test() throws InterruptedException {
		long startTime = System.currentTimeMillis();
		int threadCount = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L).posX((long)i).posY((long)i).build();
			executorService.submit(() ->{
				try{
					ticketService.reserveTicket(1L, ticketRequestDto);
					System.out.println("예약 완료");
				}finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		long endTime = System.currentTimeMillis();
		System.out.println("TicketRepository 저장 속도 = " + (endTime - startTime));
		long result = ticketInfoRepository.findById(1L).get().getStock();
		assertEquals(0L, result);
	}

	@Test
	@DisplayName("redisson 분산락을 적용하여 100명 예약 테스트")
	public void test1() throws InterruptedException{
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L).posX((long)i).posY((long)i).build();
			executorService.submit(() ->{
				try{
					redissonLockTicketFacade.reserveTicket(1L, ticketRequestDto);
				}finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		long result = ticketInfoRepository.findById(1L).get().getStock();
		assertEquals(0L, result);
	}
}
