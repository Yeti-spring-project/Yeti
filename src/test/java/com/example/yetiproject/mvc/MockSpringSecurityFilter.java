package com.example.yetiproject.mvc;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class MockSpringSecurityFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig){}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		// 가짜 인증 객체를 만들어준다.
		SecurityContextHolder.getContext()
			.setAuthentication((Authentication)((HttpServletRequest) request).getUserPrincipal());
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		SecurityContextHolder.clearContext();
	}
}
