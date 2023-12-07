package com.example.yetiproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.yetiproject.auth.LogoutSuccessHandlerImpl;
import com.example.yetiproject.auth.jwt.JwtAuthenticationFilter;
import com.example.yetiproject.auth.jwt.JwtAuthorizationFilter;
import com.example.yetiproject.auth.jwt.JwtUtil;
import com.example.yetiproject.auth.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final LogoutSuccessHandlerImpl logoutSuccessHandler;

	public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService,
		AuthenticationConfiguration authenticationConfiguration, LogoutSuccessHandlerImpl logoutSuccessHandler)
		 {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.authenticationConfiguration = authenticationConfiguration;
		this.logoutSuccessHandler = logoutSuccessHandler;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
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

		http.authorizeHttpRequests((authorizeHttpRequests) ->
			authorizeHttpRequests
				.requestMatchers("/**").permitAll()
				.anyRequest().authenticated()
		);

		// 로그인 사용
		http.formLogin((formLogin) ->
			formLogin
				//.loginPage("/login-page")
				.loginProcessingUrl("/signin")
				.permitAll()
		);

		// 로그아웃 사용 (2023-12-07 제대로 동작하지 않아 주석처리함)
		// http.logout((logout) ->
		// 	logout
		// 		.logoutUrl("/logout")
		// 		.logoutSuccessHandler(logoutSuccessHandler)
		// 		.deleteCookies("Authorization")
		// 		.clearAuthentication(true)
		// );

		// 필터 관리
		http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}