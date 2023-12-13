package com.example.yetiproject.service;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.entity.Sports;
import com.example.yetiproject.entity.Stadium;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.kafka.producer.ReserveTicketProducer;
import com.example.yetiproject.mock.WithCustomMockUser;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;

@ExtendWith(MockitoExtension.class)
public class TicketConcurrTest {
	// @InjectMocks
	// ReserveTicketProducer ticketReserveService;
	// @Mock
	// private TicketInfoRepository ticketInfoRepository;
	// @Mock
	// private TicketRepository ticketRepository;
	//
	// private static TicketRequestDto ticketRequestDto;
	//
	// @BeforeAll
	// static void beforeAll() {
	// 	ticketRequestDto = TicketRequestDto.builder()
	// 		.ticketInfoId(1L)
	// 		.posX(12L)
	// 		.posY(14L)
	// 		.build();
	// }
	//
	// @Test
	// @WithCustomMockUser
	// @DisplayName("Kafka 여러명 예매하기")
	// void test() throws InterruptedException {
	// 	//given
	// 	LocalDateTime openDateTime = LocalDateTime.of(2023, 12, 8, 12, 0); // 원하는 날짜와 시간으로 설정
	// 	LocalDateTime closeDateTime = LocalDateTime.of(2023, 12, 10, 18, 0); // 원하는 날짜와 시간으로 설정
	//
	// 	Stadium stadium = Stadium.builder().stadiumId(1L).stadiumName("고척돔").build();
	// 	Sports sports = Sports.builder().sportId(1L).stadium(stadium).build();
	// 	TicketInfo ticketInfo = TicketInfo.builder()
	// 		.sports(sports)
	// 		.ticketInfoId(1L)
	// 		.openDate(openDateTime)
	// 		.closeDate(closeDateTime)
	// 		.ticketPrice(10000L)
	// 		.stock(100L)
	// 		.build();
	//
	// 	//given(ticketInfoRepository.findById(ticketRequestDto.getTicketInfoId())).willReturn(Optional.of(ticketInfo));
	//
	// 	int threadCount = 1000; //1000명의 동시 접속자;
	// 	ExecutorService executorService = Executors.newFixedThreadPool(32); //쓰레드풀 32
	// 	//다른 스레드에서 수행해주는 작업을 기다려주는 클래스
	// 	CountDownLatch latch = new CountDownLatch(threadCount);
	//
	// 	for( int i = 0; i < threadCount; i++){
	// 		long userId = i;
	// 		executorService.submit(() -> {
	// 			try{
	// 				ticketReserveService.reserve(userId, ticketInfo.getTicketInfoId());
	// 			}finally {
	// 				latch.countDown();
	// 			}
	// 		});
	// 	}
	//
	// 	latch.await();
	// 	long count = ticketRepository.count();
	//
	// }

}
