package com.example.yetiproject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.entity.Sports;
import com.example.yetiproject.entity.Stadium;
import com.example.yetiproject.entity.Ticket;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.exception.entity.Ticket.TicketReserveException;
import com.example.yetiproject.mock.WithCustomMockUser;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {
	@InjectMocks
	private TicketServiceImpl ticketServiceImpl;
	@Mock
	private TicketRepository ticketRepository;
	@Mock
	private TicketInfoRepository ticketInfoRepository;

	private static TicketRequestDto ticketRequestDto;
	private static User user;
	@BeforeAll
	static void beforeAll() {
		ticketRequestDto = TicketRequestDto.builder()
			.ticketInfoId(1L)
			.posX(12L)
			.posY(14L)
			.build();
		user = User.builder()
			.userId(1L)
			.username("userTest")
			.build();
	}

	@Test
	@WithCustomMockUser
	@DisplayName("예매하기")
	void test(){
		//given
		LocalDateTime openDateTime = LocalDateTime.of(2023, 12, 8, 12, 0); // 원하는 날짜와 시간으로 설정
		LocalDateTime closeDateTime = LocalDateTime.of(2023, 12, 10, 18, 0); // 원하는 날짜와 시간으로 설정

		Stadium stadium = Stadium.builder().stadiumId(1L).stadiumName("고척돔").build();
		Sports sports = Sports.builder().sportId(1L).stadium(stadium).build();
		TicketInfo ticketInfo = TicketInfo.builder()
			.sports(sports)
			.openDate(openDateTime)
			.closeDate(closeDateTime)
			.ticketPrice(10000L)
			.stock(100L)
			.build();

		given(ticketInfoRepository.findById(ticketRequestDto.getTicketInfoId())).willReturn(Optional.of(ticketInfo));
		Ticket ticket = new Ticket(user, ticketInfo, ticketRequestDto);

		try {
			TicketResponseDto result = ticketServiceImpl.reserveTicket(user, ticketRequestDto);
			assertNotNull(result);
			System.out.println(result.getUserId());
			System.out.println(result.getTicketInfo().getTicketPrice());

		} catch (TicketReserveException e) {
			// 예외가 발생한 경우
			assertEquals("예약을 할 수 없습니다.", e.getMessage());
		}

	}

}
