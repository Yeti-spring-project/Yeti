package com.example.yetiproject.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.entity.Ticket;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.repository.TicketRepository;
import com.example.yetiproject.service.TicketService;

@WithMockUser
@SpringBootTest
public class DBTest {
	@MockBean
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	TicketRepository ticketRepository;
	@Autowired
	TicketService ticketService;

	@Test
	@DisplayName("MySQL을 이용하여 ticketRepository save 데이터 insert")
	void test1(){
		long startTime = System.currentTimeMillis();
		User user = User.builder().userId(1L).build();
		TicketInfo ticketInfo = TicketInfo.builder().ticketInfoId(1L).build();
		for (int i = 0; i < 50000; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
													.ticketInfoId(1L)
													.posX((long)i)
													.posY((long)i).build();
			//ticketRepository.save(new Ticket(user, ticketInfo, ticketRequestDto));
		}
		long endTime = System.currentTimeMillis();
		System.out.println("TicketRepository 저장 속도 = " + (endTime - startTime));
	}

	@Test
	@DisplayName("MySQL을 이용하여 ticketService save 티켓을 저장한다.")
	void test2(){
		long startTime = System.currentTimeMillis();
		User user = User.builder().userId(1L).build();
		for (int i = 0; i < 50000; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L)
				.posX((long)i)
				.posY((long)i).build();
			//ticketService.reserveTicket(user, ticketRequestDto);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("TicketService 저장 속도 = " + (endTime - startTime));
	}

	@Test
	@DisplayName("Postgresql을 이용하여 ticketRepository save 데이터 insert")
	void test3(){
		long startTime = System.currentTimeMillis();
		User user = User.builder().userId(1L).build();
		TicketInfo ticketInfo = TicketInfo.builder().ticketInfoId(1L).build();
		for (int i = 0; i < 50000; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L)
				.posX((long)i)
				.posY((long)i).build();
			//ticketRepository.save(new Ticket(user, ticketInfo, ticketRequestDto));
		}
		long endTime = System.currentTimeMillis();
		System.out.println("TicketRepository 저장 속도 = " + (endTime - startTime));
	}

	@Test
	@DisplayName("Postgresql을 이용하여 ticketService save 티켓을 저장한다.")
	void test4(){
		long startTime = System.currentTimeMillis();
		User user = User.builder().userId(1L).build();
		for (int i = 0; i < 50000; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L)
				.posX((long)i)
				.posY((long)i).build();
			//ticketService.reserveTicket(user, ticketRequestDto);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("TicketService 저장 속도 = " + (endTime - startTime));
	}

	@Test
	@DisplayName("Ticket User의 연관관계뒤 속도차이를 비교해본다.")
	void test5(){
		long startTime = System.currentTimeMillis();
		Long userId = 1L;
		for (int i = 0; i < 40000; i++) {
			TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
				.ticketInfoId(1L)
				.posX((long)i)
				.posY((long)i).build();
			ticketService.reserveTicket(userId, ticketRequestDto);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("[ 연관관계 제외 ] TicketService 저장 속도 = " + (endTime - startTime));
	}
}
