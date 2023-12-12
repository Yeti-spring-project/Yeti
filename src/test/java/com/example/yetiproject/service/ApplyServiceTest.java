package com.example.yetiproject.service;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import com.example.yetiproject.kafka.repository.CouponRepository;
import com.example.yetiproject.kafka.service.ApplyService;
import com.example.yetiproject.mock.WithCustomMockUser;

@ExtendWith(MockitoExtension.class)
public class ApplyServiceTest {
	@InjectMocks
	private ApplyService applyService;

	@Mock
	private CouponRepository couponRepository;

	@Test
	@WithCustomMockUser
	public void 여러명응모() throws InterruptedException {
		int threadCount = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		//다른스레드에서 수행해주는 작업을 기다려주는 클래스
		CountDownLatch latch = new CountDownLatch(threadCount);

		for( int i = 0; i < threadCount; i++){
			long userId = i;
			executorService.submit(() -> {
				try{
					System.out.println("test");
					applyService.apply(userId);
				}finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Thread.sleep(10000);
		long count = couponRepository.count();


		assertThat(count).isEqualTo(100);
	}
}
