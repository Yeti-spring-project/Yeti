package com.example.yetiproject.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.yetiproject.config.WebSecurityConfig;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.mock.WithCustomMockUser;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TicketServiceImpl.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)
	}
)
public class TicketReserveServiceTest {
	@Autowired
	private TicketServiceImpl ticketServiceImpl;
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
		TicketResponseDto ticketResponseDto = ticketServiceImpl.reserveTicket(user, ticketRequestDto);

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
