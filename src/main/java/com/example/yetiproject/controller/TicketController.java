package com.example.yetiproject.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.yetiproject.dto.ApiResponse;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.service.TicketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mytickets")
public class TicketController {
	private final TicketService ticketService;

	// TODO. user jwt 토큰 받기 필요
	// 예매한 티켓 목록 조회
	@GetMapping("")
	public ApiResponse<List<TicketResponseDto>> viewListOfReservedTickets(){
		return ApiResponse.success("예매한 티켓 목록 조회에 성공했습니다.", ticketService.getUserTicketList());
	}

	// TODO. user jwt 토큰 받기 필요
	// 예매한 티켓 상세 조회
	@GetMapping("/userId/{userId}/ticketId/{ticketId}")
	public ApiResponse<TicketResponseDto> detailViewReservedTicket(@PathVariable(name = "userId") Long userId, @PathVariable(name = "ticketId") Long ticketId){
		log.info("[Controller : detailViewReservedTicket userId = ] " + userId);
		return ApiResponse.success("티켓 상세 조회에 성공했습니다", ticketService.showDetailTicket(userId, ticketId));
	}

	// TODO. user jwt 토큰 받기 필요
	// 예매 하기
	@PostMapping("/reserve/{ticketId}")
	public ApiResponse reserveTicket(@PathVariable(name = "ticketId") Long ticketId, @RequestBody TicketRequestDto ticketRequestDto) {
		log.info("TicketController ticket_id = {}", ticketId);
		return ApiResponse.success("예매가 완료되었습니다.", ticketService.reserveTicket(ticketId, ticketRequestDto));
	}
	// TODO. user jwt 토큰 받기 필요

	// 예매 취소
	@DeleteMapping("/{ticketId}")
	public ApiResponse cancelTicket(@PathVariable(name = "ticketId") Long ticketId){
		return ApiResponse.success("예매 취소 완료" , ticketService.cancelUserTicket(ticketId));
	}

}
