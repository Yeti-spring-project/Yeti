package com.example.yetiproject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.yetiproject.dto.sports.SportsRequestDto;
import com.example.yetiproject.dto.sports.SportsResponseDto;
import com.example.yetiproject.entity.Sports;
import com.example.yetiproject.entity.Stadium;
import com.example.yetiproject.exception.entity.sports.SportsNotFoundException;
import com.example.yetiproject.repository.SportsRepository;
import com.example.yetiproject.repository.StadiumRepository;

@ExtendWith(MockitoExtension.class)
public class SportServiceTest {
	@Mock
	private SportsRepository sportsRepository;
	@Mock
	private StadiumRepository stadiumRepository;

	@InjectMocks
	private SportsService sportsService;
	static SportsRequestDto requestDto;
	@BeforeAll
	static void beforeAll() {
		requestDto = SportsRequestDto.builder()
			.stadiumId(1L)
			.sportName("2023 K리그 축구 결승")
			.matchDate("2023-12-25")
			.build();
	}

	@Test
	@DisplayName("경기 생성")
	void test1(){
		Stadium stadium = new Stadium();
		given(stadiumRepository.findById(any())).willReturn(Optional.of(stadium));

		// when
		SportsService sportsService = new SportsService(sportsRepository, stadiumRepository);
		String result = sportsService.createSports(requestDto);

		// then
		System.out.println(result);
		assertEquals("경기가 등록되었습니다", result);
	}

	@Test
	@DisplayName("경기 리스트 조회")
	void test2(){
		//when
		SportsService sportsService = new SportsService(sportsRepository, stadiumRepository);
		List<SportsResponseDto> sportsList = sportsService.getSports();

		// then
		System.out.println("sports size = " + sportsList.size());
		assertEquals(sportsList.size(), 0);
	}

	@Test
	@DisplayName("경기 상세 조회 예외 확인")
	void test3(){
		//given
		Long sportId = 1L;

		//when-then
		SportsService sportsService = new SportsService(sportsRepository, stadiumRepository);
		Exception exception = assertThrows(SportsNotFoundException.class, () ->
			sportsService.getSportsInfo(sportId));
		assertEquals("존재하지 않는 경기입니다", exception.getMessage());

	}

	@Test
	@DisplayName("경기 수정")
	void test4(){
		//given
		Long sportId = 1L;
		Stadium stadium = Stadium.builder().stadiumId(1L).stadiumName("고척돔").build();
		Sports existiingSports = Sports.builder().sportId(1L).sportName("K 스포츠").build();

		given(sportsRepository.findById(sportId)).willReturn(Optional.of(existiingSports));
		given(stadiumRepository.findById(stadium.getStadiumId())).willReturn(Optional.of(stadium));

		//when
		SportsResponseDto responseDto = sportsService.updateSport(sportId, requestDto);

		//then
		verify(sportsRepository, times(1)).findById(sportId);
		verify(stadiumRepository, times(1)).findById(stadium.getStadiumId());
		verify(sportsRepository, times(1)).save(existiingSports);
		assertEquals(responseDto.getId(), sportId);
	}


	@Test
	@DisplayName("경기 삭제")
	void test5(){
		//given
		Long sportId = 1L;
		Sports sport = new Sports();

		given(sportsRepository.findById(sportId)).willReturn(Optional.of(sport));

		//when
		String result = sportsService.deleteSport(sportId);

		//then
		assertEquals(result, "경기가 삭제되었습니다");

	}
}
