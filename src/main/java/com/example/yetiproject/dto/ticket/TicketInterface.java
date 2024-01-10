package com.example.yetiproject.dto.ticket;

import java.time.LocalDateTime;

public interface TicketInterface {
	String getUsername();
	LocalDateTime getOpenDate();
	LocalDateTime getCloseDate();
	Long getTicketPrice();
	LocalDateTime getMatchDate();
	String getSportName();
	String getStadiumName();
}
