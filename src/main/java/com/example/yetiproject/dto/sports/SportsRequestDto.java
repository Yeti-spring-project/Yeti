package com.example.yetiproject.dto.sports;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SportsRequestDto {
    private Long stadiumId;
    private String sportName;
    private LocalDateTime matchDate;
}
