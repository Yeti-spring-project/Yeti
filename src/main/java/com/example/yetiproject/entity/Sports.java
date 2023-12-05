package com.example.yetiproject.entity;

import com.example.yetiproject.dto.sports.SportsRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Sports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sportId;
    private String sportName;
    private String matchDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    public Sports(SportsRequestDto sportsRequestDto, Stadium stadium) {
        this.sportName = sportsRequestDto.getSportName();
        this.matchDate = sportsRequestDto.getMatchDate();
        this.stadium = stadium;
    }

    public void update(SportsRequestDto sportsRequestDto) {
        this.sportName = sportsRequestDto.getSportName();
        this.matchDate = sportsRequestDto.getMatchDate();
    }
}