package com.example.yetiproject.entity;

import com.example.yetiproject.dto.ticket.TicketRequestDto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "tickets")
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long ticketId;

	@Column(name="posX")
	Long posX;
	@Column(name="posY")
	Long posY;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticketInfo_id")
	private TicketInfo ticketInfo;

	public Ticket(User user, TicketInfo ticketInfo, TicketRequestDto ticketRequestDto) {
		this.posX = ticketRequestDto.getPosX();
		this.posY = ticketRequestDto.getPosY();
		this.user = user;
		this.ticketInfo = ticketInfo;
	}
}
