package com.example.yetiproject.controller;

import java.util.List;

import com.example.yetiproject.facade.RedissonLockTicketFacade;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.dto.ApiResponse;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.kafka.service.TicketKafkaService;
import com.example.yetiproject.service.TicketServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Ticket Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mytickets")
public class TicketController {
	private final TicketServiceImpl ticketServiceImpl;
	private final RedissonLockTicketFacade redissonLockTicketFacade;
	private final TicketKafkaService ticketKafkaService;

	// 예매한 티켓 목록 조회
	@GetMapping("")
	public ApiResponse<List<TicketResponseDto>> viewListOfReservedTickets(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return ApiResponse.success("예매한 티켓 목록 조회에 성공했습니다.", ticketServiceImpl.getUserTicketList(userDetails.getUser()));
	}


	// 예매한 티켓 상세 조회
	@GetMapping("/ticketId/{ticketId}")
	public ApiResponse<TicketResponseDto> detailViewReservedTicket(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable(name = "ticketId") Long ticketId){
		log.info("[Controller : detailViewReservedTicket userId = ] " + userDetails.getUser().getUserId());
		return ApiResponse.success("티켓 상세 조회에 성공했습니다", ticketServiceImpl.showDetailTicket(userDetails.getUser().getUserId(), ticketId));
	}

	//예매 하기
	@PostMapping("/reserve")
	public ApiResponse reserveTicket(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) {
		log.info("TicketController reserveTicket");
		//return ApiResponse.success("예매가 완료되었습니다.", redissonLockTicketFacade.reserveTicket(userDetails, ticketRequestDto));
		ticketKafkaService.sendReserveTicket(userDetails.getUser().getUserId(), ticketRequestDto);
		return ApiResponse.success("예매가 완료되었습니다.", null);
	}

	// 예매 취소
	@DeleteMapping("/{ticketId}")
	public ApiResponse cancelTicket(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable(name = "ticketId") Long ticketId){
		return ApiResponse.success("예매 취소 완료" , ticketServiceImpl.cancelUserTicket(userDetails.getUser(), ticketId));
	}

}
