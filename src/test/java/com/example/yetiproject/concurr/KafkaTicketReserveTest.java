package com.example.yetiproject.concurr;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.kafka.service.TicketKafkaService;
import com.example.yetiproject.repository.TicketRepository;

@SpringBootTest
public class KafkaTicketReserveTest {
	@Autowired
	private TicketKafkaService ticketKafkaService;
	@Autowired
	private TicketRepository ticketRepository;

	@Test
	@DisplayName("kafa 동시에 200명의 사용자")
	void test() throws InterruptedException{
		int threadCount = 200;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			//User user = User.builder().userId((long)i).build();
			Long userId = (long)1;
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder().ticketInfoId(1L).posY((long)i).posX((long)i).build();
			executorService.submit(() -> {
				try {
					ticketKafkaService.sendReserveTicket(userId, ticketRequestDto);
				}finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		Thread.sleep(10000);
		long count = ticketRepository.count();
		assertThat(count).isEqualTo(100);
	}
}
