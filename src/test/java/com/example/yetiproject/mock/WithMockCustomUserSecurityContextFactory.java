package com.example.yetiproject.mock;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
	@Override
	public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
		String username = annotation.username();

		Authentication auth = new UsernamePasswordAuthenticationToken(username, "",
			List.of(new SimpleGrantedAuthority("user")));
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(auth);
		return context;
	}
}
