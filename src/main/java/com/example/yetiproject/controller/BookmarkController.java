package com.example.yetiproject.controller;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.yetiproject.dto.notification.NotificationResponseDto;
import com.example.yetiproject.service.BookmarkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sports")
public class BookmarkController {

	private final BookmarkService bookmarkService;

	@PostMapping("/bookmarks")
	public ResponseEntity<String> bookmark(@RequestParam("ticketinfoid") Long ticketInfoId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bookmarkService.bookmark(ticketInfoId, userDetails.getUser().getUserId()));
	}

	// @GetMapping("/alert")
	// public void alert(@RequestParam("bookmarkId") Long bookmarkId, @RequestParam("userId") Long userId) {
	// 	bookmarkService.notificationToUser(bookmarkId, userId);
	// }
}
