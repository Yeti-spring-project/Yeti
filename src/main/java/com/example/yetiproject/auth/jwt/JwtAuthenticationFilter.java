package com.example.yetiproject.auth.jwt;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.dto.user.UserSigninRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
		setFilterProcessesUrl("/api/user/signin");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			UserSigninRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), UserSigninRequestDto.class);
			return getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(
							requestDto.getUsername(),
							requestDto.getPassword(),
							null
					)
			);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
		try {
			String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
			String token = jwtUtil.createToken(username);

			log.info("[JwtAuthenticationFilter] token : " + token);

			// 응답 헤더에 토큰 추가
			response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

			// 응답 상태 코드 및 메시지 설정
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(token);
			response.getWriter().flush();
			response.getWriter().close();

			// 로그인 redirect
//			response.sendRedirect("/api/sports/subscribe");
		} catch (IOException e) {
			log.error("IOException 발생 : " + e.getMessage());
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
		try {
			// 응답 상태 코드 및 메시지 설정
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("유저 이름 및 패스워드를 확인해주세요.");
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			log.error("IOException 발생 : " + e.getMessage());
		}
	}
}