package com.example.yetiproject.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDto {
	private Long ticketInfoId;
	private Long posX;
	private Long posY;
	private Long userId;

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
