package com.example.yetiproject.service;

import com.example.yetiproject.dto.sports.SportsResponseDto;
import com.example.yetiproject.dto.ticketinfo.TicketInfoRequestDto;
import com.example.yetiproject.dto.ticketinfo.TicketInfoResponseDto;
import com.example.yetiproject.entity.Sports;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.exception.entity.TicketInfo.TicketInfoNotFoundException;
import com.example.yetiproject.exception.entity.sports.SportsNotFoundException;
import com.example.yetiproject.repository.SportsRepository;
import com.example.yetiproject.repository.TicketInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketInfoService {

    private final TicketInfoRepository ticketInfoRepository;
    private final SportsRepository sportsRepository;

    // 티켓 정보 생성
    public TicketInfoResponseDto createTicketInfo(TicketInfoRequestDto requestDto) {
        TicketInfo ticketInfo = new TicketInfo(requestDto);

        // sports 찾기
        Sports sports = findSportsInfo(requestDto.getSportsId());

        // ticketInfo에 sports 추가
        ticketInfo.setSports(sports);

        ticketInfoRepository.save(ticketInfo);
        return new TicketInfoResponseDto(ticketInfo);
    }

    // 티켓 정보 리스트 조회
    public List<TicketInfoResponseDto> getTicketInfoList() {
        return ticketInfoRepository.findAll().stream().map(TicketInfoResponseDto::new).collect(Collectors.toList());
    }

    // 티켓 정보 상세 조회
    public TicketInfoResponseDto getTicketInfo(Long id) {
        TicketInfo ticketInfo = findTicketInfo(id);
        return new TicketInfoResponseDto(ticketInfo);
    }

    // 티켓 정보 수정
    @Transactional
    public TicketInfoResponseDto updateTicketInfo(Long id, TicketInfoRequestDto requestDto) {
        TicketInfo ticketInfo = findTicketInfo(id);
        ticketInfo.update(requestDto);
        return new TicketInfoResponseDto(ticketInfo);
    }

    // 티켓 정보 삭제
    public String deleteTicketInfo(Long id) {
        TicketInfo ticketInfo = findTicketInfo(id);
        ticketInfoRepository.delete(ticketInfo);
        return  "티켓 정보를 삭제하였습니다.";
    }

    // 티켓 정보 찾기
    private TicketInfo findTicketInfo(Long id) {
        return ticketInfoRepository.findById(id)
                .orElseThrow(() -> new TicketInfoNotFoundException("TicketInfo를 찾을 수 없습니다."));
    }

    // 스포츠 정보 찾기
    private Sports findSportsInfo(Long id) {
        return sportsRepository.findById(id)
                .orElseThrow(() -> new SportsNotFoundException("Sports를 찾을 수  없습니다."));
    }
}
