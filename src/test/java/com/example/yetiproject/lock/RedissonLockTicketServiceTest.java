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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.entity.Ticket;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.facade.RedissonLockTicketFacade;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.service.TicketService;

@SpringBootTest
public class RedissonLockTicketServiceTest {
	@MockBean
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private TicketService ticketService;
	@Autowired
	private RedissonLockTicketFacade redissonLockTicketFacade;
	@Autowired
	private TicketInfoRepository ticketInfoRepository;

	@Test
	@DisplayName("일반 ticketservice 100명 예약 테스트/ 비관적 락 / 낙관적락")
	public void test() throws InterruptedException {
		int threadCount = 500;
		//멀티스레드 이용 ExecutorService : 비동기를 단순하게 처리할 수 있또록 해주는 java api
		ExecutorService executorService = Executors.newFixedThreadPool(64);
		//다른 스레드에서 수행이 완료될 때 까지 대기할 수 있도록 도와주는 API - 요청이 끝날때 까지 기다림
		CountDownLatch latch = new CountDownLatch(threadCount);
		User user = User.builder().userId(1L).build();
		for (int i = 0; i < threadCount; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L).seat(String.valueOf(i)+i)
				.build();
			executorService.submit(() ->{
				try{
					ticketService.reserveTicket(user, ticketRequestDto);
					System.out.println("예약 완료");
				}finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		long result = ticketInfoRepository.findById(1L).get().getStock();
		assertEquals(0L, result);
	}

	@Test
	@DisplayName("redisson 분산락을 적용하여 100명 예약 테스트")
	public void test1() throws InterruptedException{
		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);
		User user = User.builder().userId(1L).build();
		for (int i = 0; i < threadCount; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L).seat(String.valueOf(i)+i)
				.build();

			executorService.submit(() ->{
				try{
					TicketResponseDto ticketResponseDto = redissonLockTicketFacade.reserveTicket(user, ticketRequestDto);
					System.out.println("( posX, posY ) = " + ticketResponseDto.getSeat());
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
