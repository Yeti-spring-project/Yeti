package com.example.yetiproject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.util.AssertionErrors.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.yetiproject.dto.stadium.StadiumModifyRequestDto;
import com.example.yetiproject.dto.stadium.StadiumResponseDto;
import com.example.yetiproject.entity.Stadium;
import com.example.yetiproject.exception.entity.stadium.StadiumNotFoundException;
import com.example.yetiproject.repository.StadiumRepository;

@ExtendWith(MockitoExtension.class)
public class StadiumServiceTest {
	@InjectMocks
	private StadiumService stadiumService;
	@Mock
	private StadiumRepository stadiumRepository;

	private static StadiumModifyRequestDto requestDto;
	@BeforeAll
	static void beforeAll() {
		requestDto = StadiumModifyRequestDto.builder()
			.stadiumName("부산 아시아드 경기장").build();
	}

	@Test
	@DisplayName("경기장 수정")
	void test(){
		//given
		Long stadiumId = 1L;
		//then
		assertThrows(StadiumNotFoundException.class, () -> {
			// 테스트 대상 메서드 호출
			stadiumService.updateStadium(stadiumId, requestDto);
		});
	}
}
