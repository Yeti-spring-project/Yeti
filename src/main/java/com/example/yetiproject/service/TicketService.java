package com.example.yetiproject.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.entity.Ticket;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.exception.entity.Ticket.TicketCancelException;
import com.example.yetiproject.exception.entity.Ticket.TicketNotFoundException;
import com.example.yetiproject.exception.entity.TicketInfo.TicketInfoNotFoundException;
import com.example.yetiproject.facade.repository.RedisRepository;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "TicketService")
@Service
@RequiredArgsConstructor
public class TicketService {
	private final TicketRepository ticketRepository;
	private final TicketInfoRepository ticketInfoRepository;
	private final RedisRepository redisRepository;

	private final String TICKETINFO_STOCK_COUNT = "ticketInfo:%s:stock";

	public List<TicketResponseDto> getUserTicketList(User user) {
		return ticketRepository.findUserTicketList(user.getUserId()).stream().map(TicketResponseDto::new).toList();
	}
	public TicketResponseDto showDetailTicket(Long userId, Long ticketId) {
		return new TicketResponseDto(ticketRepository.findUserTicket(userId, ticketId).
				orElseThrow(() -> new TicketNotFoundException("잘못된 티켓 조회입니다.")));
	}

	@Transactional
	public TicketResponseDto reserveTicket(User user, TicketRequestDto ticketRequestDto) {
		TicketInfo ticketInfo = ticketInfoRepository.findById(ticketRequestDto.getTicketInfoId()).get();
		Ticket ticket = new Ticket(user, ticketInfo, ticketRequestDto);
		ticketRepository.save(ticket);

		ticketInfo.updateStock(-1L);
		return new TicketResponseDto(ticket);
	}

	@Transactional
	public TicketResponseDto reserveTicketQueue(Long userId, TicketRequestDto ticketRequestDto) {
		TicketInfo ticketInfo = ticketInfoRepository.findById(ticketRequestDto.getTicketInfoId()).get();
		User user = User.builder().userId(userId).email("...").username("...").build();

		Ticket ticket = new Ticket(user, ticketInfo, ticketRequestDto);
		ticketRepository.save(ticket);
//		log.info("{}, {} 티켓 발급에 성공하였습니다.", ticketRequestDto.getPosX(), ticketRequestDto.getPosY());
		return new TicketResponseDto(ticket);
	}

	@Transactional
	public void reserveTicketsInBatch(List<TicketRequestDto> ticketRequestDtoList) throws JsonProcessingException {
		List<Ticket> tickets = new ArrayList<>();

		// 티켓 일괄 발급 처리
		for (TicketRequestDto ticketRequestDto : ticketRequestDtoList) {
			log.info("ticketRequestDto : {}", ticketRequestDto);
			TicketInfo ticketInfo = ticketInfoRepository.findById(ticketRequestDto.getTicketInfoId()).get();
			Long ticketCount = getTicketCounter(TICKETINFO_STOCK_COUNT.formatted(ticketInfo.getTicketInfoId()));

			if (ticketCount < ticketInfo.getStock()) {
				User user = User.builder().userId(ticketRequestDto.getUserId()).email("...").username("...").build();
				Ticket ticket = new Ticket(user, ticketInfo, ticketRequestDto);

			} else {
				log.info("[ticketInfo : " + ticketRequestDto.getTicketInfoId() + " 은 매진입니다.]");
				// 티켓이 매진된 경우 처리
				return;
			}
		}

		// 배치로 티켓 저장
		ticketRepository.saveAll(tickets);
	}

	@Transactional
	public List<Ticket> reserveTicketsInBatchList(List<TicketRequestDto> ticketRequestDtos) throws JsonProcessingException {
		List<Ticket> ticketList = new ArrayList<>();

		// 티켓 요청들에 대한 정보를 미리 가져오기
		Map<Long, TicketInfo> ticketInfoMap = new HashMap<>();
		for (TicketRequestDto ticketRequestDto : ticketRequestDtos) {
			TicketInfo ticketInfo = ticketInfoRepository.findById(ticketRequestDto.getTicketInfoId())
					.orElseThrow(() -> new TicketInfoNotFoundException("티켓 정보를 찾을 수 없습니다."));
			ticketInfoMap.put(ticketRequestDto.getTicketInfoId(), ticketInfo);
		}

		// 티켓 일괄 발급 처리
		for (TicketRequestDto ticketRequestDto : ticketRequestDtos) {
			TicketInfo ticketInfo = ticketInfoMap.get(ticketRequestDto.getTicketInfoId());
			Long ticketCount = getTicketCounter("ticketInfo" + ticketInfo.getTicketInfoId());

			if (ticketCount < ticketInfo.getStock()) {
				User user = User.builder().userId(ticketRequestDto.getUserId()).email("admin@test.com").username("jungmin").build();
				Ticket ticket = new Ticket(user, ticketInfo, ticketRequestDto);
				ticketList.add(ticket);
				incrementTicketCounter("ticketInfo" + ticketInfo.getTicketInfoId());
				redisRepository.listLeftPop("ticket");

			} else {
				log.info("[ticketInfo : " + ticketRequestDto.getTicketInfoId() + " 은 매진입니다.]");
				redisRepository.delete("ticket");
				// 티켓이 매진된 경우 처리
				return Collections.emptyList();
			}
		}

		// 배치로 티켓 저장
		return ticketRepository.saveAll(ticketList);
	}


	@Transactional
	public ResponseEntity cancelUserTicket(User user, Long ticketId) {
		Ticket ticket = ticketRepository.findUserTicket(user.getUserId(), ticketId)
			.orElseThrow(() -> new TicketNotFoundException("해당된 티켓정보가 없습니다."));
		TicketInfo ticketInfo = ticket.getTicketInfo();

		// 티켓을 산 유저와 접근한 유저가 같은지 확인
		if (!Objects.equals(user.getUserId(), ticket.getUser().getUserId())) {
			throw new RuntimeException("회원님이 구매하신 티켓이 아닙니다.");
		}

		try {
			ticketInfo.updateStock(1L);
			ticketRepository.delete(ticket);
		}catch (Exception e){
			throw new TicketCancelException("예매를 취소 할 수 없습니다.");
		}
		return ResponseEntity.ok().body("해당 티켓을 취소하였습니다.");
	}

	// 예매한 티켓 개수 증가
	public void incrementTicketCounter(String key) {
		// ValueOperations를 이용하여 INCR 명령어 실행
		redisRepository.increase(key);
	}

	// 예매한 티켓 개수 GET
	public Long getTicketCounter(String key) {
		// GET 명령어 실행 후 값을 가져오기
		String ticketCountString = redisRepository.get(key);
		Long ticketCount;

		if (Objects.equals(ticketCountString, null)) {
			// Redis에 해당 stock가 없으면 redis에 넣는다.
			redisRepository.set(key, "0");
			ticketCountString = redisRepository.get(key); // 처음에 초기화 에러 방지
		}

		ticketCount = Long.parseLong(ticketCountString);
		return ticketCount;
	}

}
