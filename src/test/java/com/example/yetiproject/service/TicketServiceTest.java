package com.example.yetiproject.service;

import static com.example.yetiproject.service.SportServiceTest.*;
import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.entity.Ticket;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;
import com.example.yetiproject.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
	@InjectMocks
	private TicketService ticketService;
	@Mock
	private TicketRepository ticketRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private TicketInfoRepository ticketInfoRepository;

	private static TicketRequestDto ticketRequestDto;
	//@BeforeAll
	// static void beforeAll() {
	// 	ticketRequestDto = TicketRequestDto.builder()
	// 		.userId(1L)
	// 		.ticketInfoId(1L)
	// 		.posX(12L)
	// 		.posY(14L)
	// 		.build();
	// }

	// @Test
	// @DisplayName("예매하기")
	// void test(){
	// 	//given
	// 	Long ticketId = 2L;
	// 	User user = User.builder().userId(1L).build();
	// 	TicketInfo ticketInfo = TicketInfo.builder().ticketInfoId(1L).build();
	// 	given(userRepository.findById(ticketRequestDto.getUserId())).willReturn(Optional.of(user));
	// 	given(ticketInfoRepository.findById(ticketRequestDto.getTicketInfoId())).willReturn(Optional.of(ticketInfo));
	//
	// 	Ticket ticket = new Ticket(ticketId, user, ticketInfo, ticketRequestDto);
	// 	//when
	// 	TicketResponseDto result = ticketService.reserveTicket(ticketId, ticketRequestDto);
	//
	// 	//then
	// 	// verify 메서드를 사용해 특정 메서드가 호출되었는지 확인할 수 있어
	// 	verify(userRepository, times(1)).findById(user.getUserId());
	// 	verify(ticketInfoRepository, times(1)).findById(ticketInfo.getTicketInfoId());
	// 	verify(ticketRepository, times(1)).save(any(Ticket.class));
	//
	// 	System.out.println("result ticketId = " + result.getTicketId());
	// 	assertNotNull(result);
	// 	assertEquals(result.getTicketId(), 2L);
	// }

	// @Test
	// @DisplayName("예매 취소")
	// void test2(){
	// 	//given
	// 	Long ticketId = 2L;
	// 	Ticket ticket = Ticket.builder().posY(12L).build();
	// 	given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
	//
	// 	//when
	// 	ResponseEntity msg = ticketService.cancelUserTicket(ticketId);
	//
	// 	//then
	// 	verify(ticketRepository, times(1)).findById(ticketId);
	// 	assertEquals("<200 OK OK,해당 티켓을 취소하였습니다.,[]>", msg.toString());
	//
	// }
}
