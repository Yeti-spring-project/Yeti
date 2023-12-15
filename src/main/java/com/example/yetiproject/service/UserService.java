package com.example.yetiproject.service;

import com.example.yetiproject.dto.user.UserSignupRequestDto;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.exception.entity.user.UserDuplicatedException;
import com.example.yetiproject.exception.entity.user.UserUnauthorizedException;
import com.example.yetiproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public void signup(UserSignupRequestDto requestDto) {
		String username = requestDto.getUsername();
		String email = requestDto.getEmail();
		String password = bCryptPasswordEncoder.encode(requestDto.getPassword());

		// 이름 중복 확인
		userRepository.findByUsername(username)
				.ifPresent(user -> {throw new UserDuplicatedException("이미 사용중인 아이디입니다.");});

		// 이메일 중복 확인
		userRepository.findByEmail(email)
				.ifPresent(user -> {throw new UserDuplicatedException("이미 사용중인 이메일입니다.");});

		// 비밀번호 두 개가 다르면 에러 발생
		if (!StringUtils.equals(requestDto.getPassword(), requestDto.getPassword2())) {
			throw new UserUnauthorizedException("두 비밀번호가 다릅니다.");
		}

		// 유저 객체를 만든 후 레포지토리에 저장
		User user = new User(requestDto, password);
//		user.updatePassword(bCryptPasswordEncoder.encode(requestDto.getPassword()));
		userRepository.save(user);
	}
}
