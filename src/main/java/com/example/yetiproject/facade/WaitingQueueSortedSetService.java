package com.example.yetiproject.facade;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.entity.Seat;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.exception.ErrorCode;
import com.example.yetiproject.facade.repository.RedisRepository;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "WaitingQueueSortedSetService")
@Service
@RequiredArgsConstructor
public class WaitingQueueSortedSetService {
	private final RedisRepository redisRepository;
	private final TicketInfoRepository ticketInfoRepository;
	private final ObjectMapper objectMapper;
	private static final long FIRST_ELEMENT = 0;
	private static final long LAST_ELEMENT = -1;

	private final String USER_QUEUE_WAIT_KEY = "ticketInfo:queue:%s:wait";

	private final String TICKETINFO_OCCUPY_SEAT = "ticketInfo:%s:reserved:seat";
	private final String TICKETINFO_STOCK_COUNT = "ticketInfo:%s:stock";

	public Long registerQueue(TicketRequestDto ticketRequestDto) throws JsonProcessingException {
		// 좌석 체크
		String seatObject = objectMapper.writeValueAsString(new Seat(ticketRequestDto.getPosX(), ticketRequestDto.getPosY()));
		if(checkSelectedSeat(TICKETINFO_OCCUPY_SEAT.formatted(ticketRequestDto.getTicketInfoId())
			, seatObject, ticketRequestDto.getUserId()) == false){
			log.info("이미 선택된 좌석입니다.");
			throw ErrorCode.QUEUE_ALREADY_REGISTERED_USER.build();
		}
		//오픈날짜 종료날짜를 체크한다.
		if(checkTicketInfoDate(ticketRequestDto.getTicketInfoId()) == false){
			log.info("예매가능한 날짜가 아닙니다.");
			throw ErrorCode.NOT_AVAILABLE_RESERVATION_DATES.build();
		}
		// redis ticketInfo check
		setTicketStock(ticketRequestDto.getTicketInfoId());

		//객체 -> String 변형
		String jsonObject = objectMapper.writeValueAsString(ticketRequestDto); //ticketRequestDto -> String
		double timeStamp = Instant.now().getEpochSecond();
		log.info("대기열에 추가 - userId : {} requestDto : {} ({}초)", ticketRequestDto.getUserId(), jsonObject, System.currentTimeMillis());

		redisRepository.zAddIfAbsent(USER_QUEUE_WAIT_KEY.formatted(ticketRequestDto.getTicketInfoId()), jsonObject, timeStamp);
		return redisRepository.zRank(USER_QUEUE_WAIT_KEY.formatted(ticketRequestDto.getTicketInfoId()),jsonObject);
	}

	private void setTicketStock(Long ticketInfoId) throws JsonProcessingException {
		// Redis에 해당 stock가 없으면 redis에 넣기
		TicketInfo ticketInfo = ticketInfoRepository.findById(ticketInfoId).get();
		if(redisRepository.get(TICKETINFO_STOCK_COUNT.formatted(ticketInfoId))==null){
			// Redis에 해당 stock가 없으면 redis에 넣는다.
			redisRepository.set(TICKETINFO_STOCK_COUNT.formatted(ticketInfoId), String.valueOf(ticketInfo.getStock()));
		}
	}

	public void getWaitingNumber() throws JsonProcessingException {
		final long start = FIRST_ELEMENT;
		final long end = LAST_ELEMENT;

		Set<String> queue = redisRepository.zRange("ticket", start, end);
		for ( String ticketRequest : queue) {
			Long rank = redisRepository.zRank("ticket", ticketRequest);
			//log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", ticketRequest, rank);
		}
	}

	public Long getRank(Long ticketInfoId, Long userId, Long posX, Long posY) throws JsonProcessingException {
		String jsonObject = objectMapper.writeValueAsString(new TicketRequestDto(userId, ticketInfoId, posX, posY));
		Long rank = redisRepository.zRank(USER_QUEUE_WAIT_KEY.formatted(ticketInfoId), jsonObject);
		if(rank == null){
			return -1L;
		}
		return rank;
	}
	private Boolean checkTicketInfoDate(Long ticketInfoId){
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openDate = ticketInfoRepository.findById(ticketInfoId).get().getOpenDate();
		LocalDateTime closeDate = ticketInfoRepository.findById(ticketInfoId).get().getCloseDate();

		if((ChronoUnit.SECONDS.between(openDate, today) > 0) &&
				(ChronoUnit.SECONDS.between(today, closeDate) > 0)){
			return true;
		}
		return false;
	}

	private boolean checkSelectedSeat(String key, String seat, Long userId){
		return redisRepository.hashSetNx(key, seat, userId);
	}
}
