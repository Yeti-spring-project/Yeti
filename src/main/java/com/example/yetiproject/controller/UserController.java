package com.example.yetiproject.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.yetiproject.dto.user.UserSigninRequestDto;
import com.example.yetiproject.dto.user.UserSignupRequestDto;
import com.example.yetiproject.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;

	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody UserSignupRequestDto requestDto) {
		userService.signup(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body("회원가입에 성공하였습니다.");
	}
}
