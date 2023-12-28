package com.example.yetiproject.dto.ticket;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDto {
	private Long userId;
	private Double now;
	private Long ticketInfoId;
	private Long posX;
	private Long posY;

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
