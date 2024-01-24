package com.example.yetiproject.dto.ticket;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDto {
	private Long userId;
	private Long ticketInfoId;
	private String seat;


	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
