package com.example.yetiproject.facade.issue;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.facade.repository.RedisRepository;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j(topic = "TicketIssueService")
@Service
@RequiredArgsConstructor
public class TicketIssueListService {
	private final RedisRepository redisRepository;
	private final TicketService ticketService;
	private final ObjectMapper objectMapper;
	private final TicketInfoRepository ticketInfoRepository;
	private static final long FIRST_ELEMENT = 0;
	private static final long PUBLISH_SIZE = 100;
	private static final long LAST_INDEX = 1;


	@Transactional
	public void publish() throws JsonProcessingException {
		final long start = FIRST_ELEMENT;
		final long end = PUBLISH_SIZE - LAST_INDEX;

		List<String> queue = redisRepository.listRange("ticket", start, end);
		List<TicketRequestDto> ticketRequests = new ArrayList<>();

		for (String ticketRequest : queue) {
			TicketRequestDto ticketRequestDto = objectMapper.readValue(ticketRequest, TicketRequestDto.class);
			ticketRequests.add(ticketRequestDto);
		}

		// 티켓 일괄 발급
		//ticketService.reserveTicketsInBatchList(ticketRequests);
	}

	public Long decrease(Long ticketInfoId){
		return redisRepository.decrease("ticketInfo"+ticketInfoId);
	}
}
