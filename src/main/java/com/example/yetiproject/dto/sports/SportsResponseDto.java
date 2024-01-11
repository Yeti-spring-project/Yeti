package com.example.yetiproject.dto.sports;

import java.time.LocalDateTime;

import com.example.yetiproject.entity.Sports;
import com.example.yetiproject.entity.Stadium;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SportsResponseDto {
    private Long id;
    private String sportsName;
    private LocalDateTime matchDate;
    private Long stadiumId;
    private String stadiumName;

    public SportsResponseDto(Sports sports) {
        this.id = sports.getSportId();
        this.sportsName = sports.getSportName();
        this.matchDate = sports.getMatchDate();
        this.stadiumId = sports.getStadium().getStadiumId();
        this.stadiumName = sports.getStadium().getStadiumName();

    }
}
