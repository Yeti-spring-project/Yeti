package com.example.yetiproject.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sports")
public class NotificationController {

	private final NotificationService notificationService;

	// SSE 수신을 위해 서버에 연결
	@GetMapping(value = "/subscribe", produces = "text/event-stream")
	public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
		return notificationService.subscribe(userDetails.getUser().getUserId(), lastEventId);
	}

	@GetMapping("/")
	public void abc(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		notificationService.send("sf", userDetails.getUser());
	}
}
