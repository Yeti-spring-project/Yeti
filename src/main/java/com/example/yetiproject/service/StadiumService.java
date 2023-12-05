package com.example.yetiproject.service;

import com.example.yetiproject.dto.stadium.StadiumCreateRequestDto;
import com.example.yetiproject.dto.stadium.StadiumModifyRequestDto;
import com.example.yetiproject.dto.stadium.StadiumResponseDto;
import com.example.yetiproject.entity.Stadium;
import com.example.yetiproject.exception.entity.stadium.StadiumNotFoundException;
import com.example.yetiproject.repository.StadiumRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StadiumService {

	private final StadiumRepository stadiumRepository;

	// 경기장 생성
	public StadiumResponseDto createStadium(StadiumCreateRequestDto requestDto) {
		Stadium stadium = new Stadium(requestDto);
		stadiumRepository.save(stadium);
		return new StadiumResponseDto(stadium);
	}

	// 경기장 조회
	public StadiumResponseDto getStadium(Long stadiumId) {
		Stadium stadium = findStadium(stadiumId);
		return new StadiumResponseDto(stadium);
	}

	// 경기장 수정
	@Transactional
	public void updateStadium(Long stadiumId, StadiumModifyRequestDto requestDto) {
		Stadium stadium = findStadium(stadiumId);
		stadium.update(requestDto);
	}

	// 경기장 삭제
	public void deleteStadium(Long stadiumId) {
		Stadium stadium = findStadium(stadiumId);
		stadiumRepository.delete(stadium);
	}

	// 경기장 찾기
	private Stadium findStadium(Long id) {
		Stadium stadium = stadiumRepository.findById(id).orElseThrow(
				() -> new StadiumNotFoundException("존재하지 않는 경기장입니다."));
		return stadium;
	}
}
