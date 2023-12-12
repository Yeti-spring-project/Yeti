package com.example.yetiproject.service;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.config.WebSecurityConfig;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.kafka.repository.CouponRepository;
import com.example.yetiproject.mock.WithCustomMockUser;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TicketService.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)
	}
)
public class TicketReserveServiceTest {
	@Autowired
	private TicketService ticketService;
	@Autowired
	private TicketInfoRepository ticketInfoRepository;

	@Autowired
	private TicketRepository ticketRepository;

	// @BeforeEach
	// void setUp(){
	// 	User user = User.builder().userId(1L).username("administrator").password("@Qkrwjdals96").build();
	// 	UserDetails userDetails = new UserDetailsImpl(user);
	// 	SecurityContext context = SecurityContextHolder.getContext();
	// 	context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), userDetails.getAuthorities()));
	// }

	@Test
	@WithCustomMockUser
	@DisplayName("예매하기")
	void test(){
		//given
		User user = User.builder()
						.userId(1L)
			.address("111-400").password("$2a$10$OsOQs1YdbyI7K5f6Jr.bGeJBzc20GiR1jU5qmVC6/sFe5VdHsYyu6").password("010-1111-1111")
			.username("jungmin").email("admin@test.com").build();
		TicketInfo ticketInfo = new TicketInfo();
		TicketRequestDto ticketRequestDto = TicketRequestDto.builder().ticketInfoId(1L).posY(12L).posY(20L).build();

		//when
		TicketResponseDto ticketResponseDto = ticketService.reserveTicket(user, ticketRequestDto);

		System.out.println(ticketResponseDto.getUserId());
	}


	// @Test
	// @DisplayName("여러명이 동시에 예매하기")
	// void test1() throws InterruptedException{
	// 	//given
	// 	int threadCount = 1000;
	// 	ExecutorService executorService = Executors.newFixedThreadPool(32);
	// 	CountDownLatch latch = new CountDownLatch(threadCount);
	//
	// 	// when
	// 	for (int i = 0; i < threadCount; i++) {
	// 		long userId = i;
	// 		User user = User.builder().userId((long)i).build();
	//
	// 		TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
	// 			.ticketInfoId(1L).posX((long)i).posY((long)i).build();
	// 		executorService.submit(() -> {
	// 			try{
	// 				ticketService.reserveTicket(user, ticketRequestDto);
	// 			}finally {
	// 				latch.countDown();
	// 			}
	// 		});
	// 	}
	//
	// 	latch.await();
	// 	long count = couponRepository.count();
	// 	assertThat(count).isEqualTo(100);
	// }
}
