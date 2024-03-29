package com.example.yetiproject.config;

import com.example.yetiproject.auth.jwt.JwtAuthenticationFilter;
import com.example.yetiproject.auth.jwt.JwtAuthorizationFilter;
import com.example.yetiproject.auth.jwt.JwtUtil;
import com.example.yetiproject.auth.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.yetiproject.auth.LogoutSuccessHandlerImpl;
import com.example.yetiproject.auth.jwt.JwtAuthenticationFilter;
import com.example.yetiproject.auth.jwt.JwtAuthorizationFilter;
import com.example.yetiproject.auth.jwt.JwtUtil;
import com.example.yetiproject.auth.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor// Spring Security 지원을 가능하게 함
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthenticationConfiguration authenticationConfiguration;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
		filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
		return filter;
	}

	@Bean
	public JwtAuthorizationFilter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF 설정
		http.csrf((csrf) -> csrf.disable());

		// 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
		http.sessionManagement((sessionManagement) ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		http.authorizeHttpRequests((authorizeHttpRequests) ->
			authorizeHttpRequests
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
				.requestMatchers("/").permitAll() // 메인 페이지 요청 허가
				.requestMatchers("/actuator/**").permitAll() // 프로메테우스 요청 허가
				.requestMatchers(HttpMethod.GET, "/api/tickets/**").permitAll()
				.requestMatchers( "/api/mytickets/**").permitAll()
				.requestMatchers("/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
				.requestMatchers("/manager/**").permitAll()
				.requestMatchers("/api/search/**").permitAll()
				.requestMatchers("/topic/**").permitAll()

				.anyRequest().authenticated() // 그 외 모든 요청 인증처리
		);

		// 로그인 사용
		http.formLogin((formLogin) ->
			formLogin
				//.loginPage("/login-page")
				.loginProcessingUrl("/signin")
				.permitAll()
		);


		// 필터 관리
		http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}