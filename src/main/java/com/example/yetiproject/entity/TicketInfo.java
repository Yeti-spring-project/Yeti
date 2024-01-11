package com.example.yetiproject.entity;

import com.example.yetiproject.dto.ticketinfo.TicketInfoRequestDto;
import com.example.yetiproject.exception.entity.TicketInfo.OutOfStockException;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket_info")
public class TicketInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketInfoId;

    @Column(nullable = false)
    private Long ticketPrice;

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private LocalDateTime openDate;

    @Column(nullable = false)
    private LocalDateTime closeDate;

    @Column(name = "sport_name")
    private String sportName;

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "sports_id")
    // private Sports sports;

    public TicketInfo(TicketInfoRequestDto ticketRequestDto) {
        this.ticketPrice = ticketRequestDto.getTicketPrice();
        this.stock = ticketRequestDto.getStock();
        this.openDate = ticketRequestDto.getOpenDate();
        this.closeDate = ticketRequestDto.getCloseDate();
        this.sportName = ticketRequestDto.getSportName();
        this.matchDate = ticketRequestDto.getMatchDate();
    }

    public void update(TicketInfoRequestDto requestDto) {
        this.ticketPrice = requestDto.getTicketPrice();
        this.stock = requestDto.getStock();
        this.openDate = requestDto.getOpenDate();
        this.closeDate = requestDto.getCloseDate();
        this.sportName = requestDto.getSportName();
        this.matchDate = requestDto.getMatchDate();
    }

    @Transactional
	public void updateStock(Long amount) {
        if (this.stock + amount < 0) {
            throw new OutOfStockException("재고 소진");
        }
        this.stock += amount;
	}

    public void updateStockCount(Long amount){
        this.stock = amount;
    }
}
