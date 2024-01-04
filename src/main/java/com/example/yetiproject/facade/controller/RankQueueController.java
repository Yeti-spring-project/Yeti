package com.example.yetiproject.facade.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.entity.Ticket;
import com.example.yetiproject.facade.WaitingQueueListBulkService;
import com.example.yetiproject.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/reserve")
@Slf4j(topic = "대기열 SSE")
public class RankQueueController {

	private final NotificationService notificationService;
	private final WaitingQueueListBulkService waitingQueueListBulkService;

	@GetMapping(value = "/waiting-queue", produces = "text/event-stream")
	public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TicketRequestDto ticketRequestDto,
		@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) throws JsonProcessingException {
		waitingQueueListBulkService.registerQueue(ticketRequestDto.getUserId(), ticketRequestDto);
		// return notificationService.subscribe(userDetails.getUser().getUserId(), lastEventId);
		return notificationService.subscribe(ticketRequestDto.getUserId(), lastEventId);
	}
}
