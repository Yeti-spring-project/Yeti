package com.example.yetiproject.facade;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class QueueObject {
    private Long userId;
    private Double now;
    private Long ticketInfoId;
    private Long posX;
    private Long posY;

    public QueueObject(UserDetailsImpl userDetails, TicketRequestDto ticketRequestDto, double now) {
        this.userId = userDetails.getUser().getUserId();
        this.now = now;
        this.ticketInfoId = ticketRequestDto.getTicketInfoId();
        this.posX = ticketRequestDto.getPosX();
        this.posY = ticketRequestDto.getPosY();
    }
}
