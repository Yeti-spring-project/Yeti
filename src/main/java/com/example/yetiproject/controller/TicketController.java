package com.example.yetiproject.controller;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.dto.ApiResponse;
import com.example.yetiproject.dto.ticket.TicketInterface;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.dto.user.RegisterUserResponse;
import com.example.yetiproject.facade.*;
import com.example.yetiproject.facade.sortedset.WaitingQueueService;
import com.example.yetiproject.facade.sortedset.WaitingQueueSortedSetService;
import com.example.yetiproject.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.type.descriptor.jdbc.OracleJsonBlobJdbcType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "TicketController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mytickets")
public class TicketController {
	private final TicketService ticketService;
	private final RedissonLockTicketFacade redissonLockTicketFacade;
	private final WaitingQueueService waitingQueueService;
	private final WaitingQueueListService waitingQueueListService;
	private final WaitingQueueListBulkService waitingQueueListBulkService;
	private final WaitingQueueSortedSetService waitingQueueSortedSetService;

	// 예매한 티켓 목록 조회
	@GetMapping("")
	public ApiResponse<List<Object>> viewListOfReservedTickets(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return ApiResponse.success("예매한 티켓 목록 조회에 성공했습니다.", ticketService.getUserTicketList(userDetails.getUser()));
	}

	// 예매한 티켓 상세 조회
	@GetMapping("/ticketId/{ticketId}")
	public ApiResponse<Object> detailViewReservedTicket(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable(name = "ticketId") Long ticketId){
		return ApiResponse.success("티켓 상세 조회에 성공했습니다", ticketService.showDetailTicket(userDetails.getUser().getUserId(), ticketId));
	}

	// 예매 - redisson
	@PostMapping("/reserve")
	public ApiResponse reserveTicket(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) {
		return ApiResponse.success("예매가 완료되었습니다.", redissonLockTicketFacade.reserveTicket(userDetails.getUser().getUserId(), ticketRequestDto));
	}

	// redis sortedset 날짜확인X, 좌석체크X
	@PostMapping("/reserve/queue/sortedset")
	public RegisterUserResponse reserveTicketQueue(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) throws JsonProcessingException {
		waitingQueueService.registerQueue(userDetails.getUser().getUserId(), ticketRequestDto);
		return new RegisterUserResponse(waitingQueueService.registerQueue(userDetails.getUser().getUserId(), ticketRequestDto));
	}

	//jungmin sorted set 날짜체크O, 좌석체크O
	@PostMapping("/reserve/waiting/queue/sortedset")
	public RegisterUserResponse reserveTicketQueueSortedSet(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) throws JsonProcessingException {
		// user는 jwt 인증으로만 사용한다.
		return new RegisterUserResponse(waitingQueueSortedSetService.registerQueue(userDetails.getUser().getUserId(), ticketRequestDto));
	}

	// redis list
	@PostMapping("/reserve/queue/list")
	public ApiResponse reserveTicketQueueList(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) throws JsonProcessingException {
		waitingQueueListService.addQueue(userDetails.getUser(), ticketRequestDto);
		return ApiResponse.successWithNoContent("예매가 완료되었습니다.");
	}

	@PostMapping("/reserve/queue/list/bulk")
	public ApiResponse reserveTicketQueueListBulk(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) throws JsonProcessingException {
		// log.info("queue start : {}", System.currentTimeMillis());
		waitingQueueListBulkService.registerQueue(userDetails.getUser().getUserId(), ticketRequestDto);
		return ApiResponse.success("예매 완료", null);
	}

	@GetMapping("/rank")
	public Long getRankUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam(name="ticketInfo_id") Long ticketInfoId,
		@RequestParam(name="user_id") Long userId,
		@RequestParam(name="posx") Long posX,
		@RequestParam(name = "posy") Long posY) throws JsonProcessingException {
		return waitingQueueSortedSetService.getRank(ticketInfoId, userId, posX, posY);
	}

	// 예매 취소
	@DeleteMapping("/{ticketId}")
	public void cancelTicket(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable(name = "ticketId") Long ticketId){
		//return ApiResponse.success("예매 취소 완료" , ticketService.cancelUserTicket(userDetails.getUser(), ticketId));
	}

}
