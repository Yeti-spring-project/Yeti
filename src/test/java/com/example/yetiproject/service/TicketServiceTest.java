package com.example.yetiproject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.dto.ticketinfo.TicketInfoRequestDto;
import com.example.yetiproject.entity.Sports;
import com.example.yetiproject.entity.Stadium;
import com.example.yetiproject.entity.Ticket;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.exception.entity.Ticket.TicketReserveException;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
	@InjectMocks
	private TicketService ticketService;
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
		// 예외 테스트를 위해 Repository의 save 메소드에서 예외 던지도록 설정
		doThrow(new RuntimeException("예외 발생")).when(ticketRepository).save(ticket);

		try {
			TicketResponseDto result = ticketService.reserveTicket(user, ticketRequestDto);
			assertNotNull(result);
		} catch (TicketReserveException e) {
			// 예외가 발생한 경우
			assertEquals("예약을 할 수 없습니다.", e.getMessage());
		}

	}

	// @Test
	// @DisplayName("예매 취소")
	// void test2(){
	// 	//given
	// 	Long ticketId = 3L;
	// 	Ticket ticket = Ticket.builder().posY(17L).build();
	// 	given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
	//
	// 	//when
	// 	ResponseEntity msg = ticketService.cancelUserTicket(user, ticketId);
	//
	// 	//then
	// 	verify(ticketRepository, times(1)).findById(ticketId);
	// 	assertEquals("<200 OK OK,해당 티켓을 취소하였습니다.,[]>", msg.toString());
	//
	// }
}
