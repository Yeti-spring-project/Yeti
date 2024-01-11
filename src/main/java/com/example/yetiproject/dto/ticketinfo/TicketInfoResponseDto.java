package com.example.yetiproject.dto.ticketinfo;

import com.example.yetiproject.dto.sports.SportsResponseDto;
import com.example.yetiproject.entity.TicketInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import net.bytebuddy.asm.Advice;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TicketInfoResponseDto {
    private Long id;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private Long ticketPrice;
    private Long stock;
    private String sportName;
    private LocalDateTime matchDate;

    public TicketInfoResponseDto(TicketInfo ticketInfo) {
        this.id = ticketInfo.getTicketInfoId();
        this.openDate = ticketInfo.getOpenDate();
        this.closeDate = ticketInfo.getCloseDate();
        this.ticketPrice = ticketInfo.getTicketPrice();
        this.stock = ticketInfo.getStock();
        this.sportName = ticketInfo.getSportName();
        this.matchDate = ticketInfo.getMatchDate();
    }
}
