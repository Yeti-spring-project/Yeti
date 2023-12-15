// package com.example.yetiproject.mvc;
//
// import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.security.Principal;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.FilterType;
// import org.springframework.test.context.web.WebAppConfiguration;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.web.context.WebApplicationContext;
//
// import com.example.yetiproject.config.WebSecurityConfig;
// import com.example.yetiproject.controller.UserController;
// import com.example.yetiproject.service.UserService;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
//
// @WebMvcTest(
// 	// usercontroller 통합 테스트를 진행
// 	controllers = UserController.class,
// 	excludeFilters = {
// 		@ComponentScan.Filter(
// 			type= FilterType.ASSIGNABLE_TYPE,
// 			classes = WebSecurityConfig.class
// 		)
// 	}
// )
// class UserMvcTest {
// 	private MockMvc mvc;
// 	private Principal mockPrincipal;
// 	@Autowired
// 	private WebApplicationContext context;
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@MockBean
// 	UserService userService;
//
// 	@BeforeEach
// 	public void setup(){
// 		mvc = MockMvcBuilders.webAppContextSetup(context)
// 			.apply(springSecurity(new MockSpringSecurityFilter()))
// 			.build();
// 	}
//
// 	@Test
// 	@DisplayName("로그인 확인")
// 	void test1() throws Exception{
// 		//when-then
// 		mvc.perform(get("/signin"))
// 			.andExpect(status().isOk())
// 			.andDo(print());
// 	}
// }
