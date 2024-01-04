package com.example.yetiproject.controller;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.dto.ApiResponse;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.facade.*;
import com.example.yetiproject.facade.RedissonLockTicketFacade;
import com.example.yetiproject.facade.WaitingQueueListService;
import com.example.yetiproject.facade.WaitingQueueService;
import com.example.yetiproject.facade.WaitingQueueSortedSetService;
import com.example.yetiproject.service.NotificationService;
import com.example.yetiproject.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j(topic = "Ticket Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mytickets")
public class TicketController {
	private final TicketService ticketService;
//	private final TicketKafkaService ticketKafkaService;
	private final RedissonLockTicketFacade redissonLockTicketFacade;
	private final WaitingQueueService waitingQueueService;
	private final WaitingQueueListService waitingQueueListService;
	private final WaitingQueueListBulkService waitingQueueListBulkService;
	private final WaitingQueueSortedSetService waitingQueueSortedSetService;

	// SSE
	private final NotificationService notificationService;

	// 예매한 티켓 목록 조회
	@GetMapping("")
	public ApiResponse<List<TicketResponseDto>> viewListOfReservedTickets(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return ApiResponse.success("예매한 티켓 목록 조회에 성공했습니다.", ticketService.getUserTicketList(userDetails.getUser()));
	}


	// 예매한 티켓 상세 조회
	@GetMapping("/ticketId/{ticketId}")
	public ApiResponse<TicketResponseDto> detailViewReservedTicket(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable(name = "ticketId") Long ticketId){
		log.info("[Controller : detailViewReservedTicket userId = ] " + userDetails.getUser().getUserId());
		return ApiResponse.success("티켓 상세 조회에 성공했습니다", ticketService.showDetailTicket(userDetails.getUser().getUserId(), ticketId));
	}

	// 예매 - redisson
	@PostMapping("/reserve")
	public ApiResponse reserveTicket(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) {
		return ApiResponse.success("예매가 완료되었습니다.", redissonLockTicketFacade.reserveTicket(userDetails, ticketRequestDto));
	}

	// 예매 - kafka
//	@PostMapping("/reserve/kafka")
//	public ApiResponse reserveTicketKafka(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) {
//		ticketKafkaService.sendReserveTicket(userDetails.getUser().getUserId(), ticketRequestDto);
//		return ApiResponse.success("예매가 완료되었습니다.", null);
//	}

	// 예매 - redis queue
	@PostMapping("/reserve/queue")
	public ApiResponse reserveTicketQueue(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) throws JsonProcessingException {
//		log.info("queue start : {}", System.currentTimeMillis());
		waitingQueueService.addQueue(userDetails.getUser(), ticketRequestDto);
		return ApiResponse.successWithNoContent("예매가 완료되었습니다.");
	}

	@PostMapping("/reserve/queue/list")
	public ApiResponse reserveTicketQueueList(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) throws JsonProcessingException {
//		log.info("queue start : {}", System.currentTimeMillis());
		waitingQueueListService.addQueue(userDetails.getUser(), ticketRequestDto);
		return ApiResponse.successWithNoContent("예매가 완료되었습니다.");
	}

	//jungmin sorted set
	@PostMapping("/reserve/queue/sortedset")
	public ApiResponse reserveTicketQueueSortedSet(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) throws JsonProcessingException {
		// log.info("queue start : {}", System.currentTimeMillis());
		waitingQueueSortedSetService.registerQueue(userDetails.getUser().getUserId(), ticketRequestDto);
		return ApiResponse.success("예매 완료", null);
	}

	@PostMapping("/reserve/queue/list/bulk")
	public ApiResponse reserveTicketQueueListBulk(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto) throws JsonProcessingException {
		// log.info("queue start : {}", System.currentTimeMillis());
		waitingQueueListBulkService.registerQueue(userDetails.getUser().getUserId(), ticketRequestDto);
		return ApiResponse.success("예매 완료", null);
	}

	// 예매 취소
	@DeleteMapping("/{ticketId}")
	public ApiResponse cancelTicket(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable(name = "ticketId") Long ticketId){
		return ApiResponse.success("예매 취소 완료" , ticketService.cancelUserTicket(userDetails.getUser(), ticketId));
	}

}
