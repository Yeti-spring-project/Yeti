package com.example.yetiproject.dto.ticket;

import com.example.yetiproject.entity.Ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDto {
	Long ticketId;
	Long posX;
	Long posY;
	Long userId;
	Long ticketInfoId;

	public TicketResponseDto(Ticket ticket) {
		this.ticketId = ticket.getTicketId();
		this.posX = ticket.getPosX();
		this.posY = ticket.getPosY();
		this.userId = ticket.getUser().getUserId();
		this.ticketInfoId = ticket.getTicketInfo().getTicketInfoId();
	}
}