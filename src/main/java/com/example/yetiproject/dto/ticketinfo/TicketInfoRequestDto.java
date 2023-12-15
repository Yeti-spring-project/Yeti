package com.example.yetiproject.dto.ticketinfo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketInfoRequestDto {
    private Long sportsId;
    private Long ticketPrice;
    private Long stock;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
}
