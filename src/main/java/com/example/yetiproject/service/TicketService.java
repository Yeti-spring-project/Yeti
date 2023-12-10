package com.example.yetiproject.service;

import java.util.List;
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
import com.example.yetiproject.exception.entity.Ticket.TicketReserveException;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;
import com.example.yetiproject.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final TicketRepository ticketRepository;
	private final TicketInfoRepository ticketInfoRepository;
	public List<TicketResponseDto> getUserTicketList(User user) {
		return ticketRepository.findUserTicketList(user.getUserId()).stream().map(TicketResponseDto::new).toList();
	}
	public TicketResponseDto showDetailTicket(Long userId, Long ticketId) {
		return new TicketResponseDto(ticketRepository.findUserTicket(userId, ticketId).
				orElseThrow(() -> new TicketNotFoundException("잘못된 티켓 조회입니다.")));
		//return new TicketResponseDto(ticketRepository.findUserShowDetailTicket(userId, ticketId));
	}

	@Transactional
	public TicketResponseDto reserveTicket(User user, TicketRequestDto ticketRequestDto) {
		TicketInfo ticketInfo = ticketInfoRepository.findById(ticketRequestDto.getTicketInfoId()).get();
		Ticket ticket = new Ticket(user, ticketInfo, ticketRequestDto);
		try {
			ticketInfo.updateStock(-1L); // 티켓 총 개수 차감
			ticketRepository.save(ticket);
		}catch (Exception e){
			throw new TicketReserveException("예약을 할 수 없습니다.");
		}
		return new TicketResponseDto(ticket);
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
			new TicketCancelException("예매를 취소 할 수 없습니다.");
		}
		return ResponseEntity.ok().body("해당 티켓을 취소하였습니다.");
	}
}
