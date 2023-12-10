package com.example.yetiproject.controller;

import com.example.yetiproject.dto.ApiResponse;
import com.example.yetiproject.dto.ticketinfo.TicketInfoRequestDto;
import com.example.yetiproject.dto.ticketinfo.TicketInfoResponseDto;
import com.example.yetiproject.service.TicketInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketInfoController {

    private final TicketInfoService ticketInfoService;


    @PostMapping("")
    public ApiResponse<TicketInfoResponseDto> createTicketInfo(@RequestBody TicketInfoRequestDto requestDto) {
        return ApiResponse.success("티켓 생성에 성공하였습니다.", ticketInfoService.createTicketInfo(requestDto));
    }

    @GetMapping("/{id}")
    public ApiResponse<TicketInfoResponseDto> getTicketInfo(@PathVariable(name = "id") Long id){
        return ApiResponse.success("티켓 조회에 성공하였습니다.", ticketInfoService.getTicketInfo(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<TicketInfoResponseDto> updateTicketInfo(@PathVariable(name = "id") Long id, @RequestBody TicketInfoRequestDto requestDto) {
        return ApiResponse.success("티켓 수정에 성공하였습니다.", ticketInfoService.updateTicketInfo(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteTicketInfo(@PathVariable(name = "id") Long id) {
        return ApiResponse.successWithNoContent(ticketInfoService.deleteTicketInfo(id));
    }

}
