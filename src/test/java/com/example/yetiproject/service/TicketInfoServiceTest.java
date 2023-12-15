// package com.example.yetiproject.service;
//
// import com.example.yetiproject.dto.ticketinfo.TicketInfoRequestDto;
// import com.example.yetiproject.dto.ticketinfo.TicketInfoResponseDto;
// import com.example.yetiproject.entity.Sports;
// import com.example.yetiproject.entity.Stadium;
// import com.example.yetiproject.entity.TicketInfo;
// import com.example.yetiproject.exception.entity.TicketInfo.TicketInfoNotFoundException;
// import com.example.yetiproject.repository.SportsRepository;
// import com.example.yetiproject.repository.StadiumRepository;
// import com.example.yetiproject.repository.TicketInfoRepository;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import java.time.LocalDateTime;
// import java.util.Optional;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
//
// @ExtendWith(MockitoExtension.class)
// public class TicketInfoServiceTest {
//
//     @Mock
//     private SportsRepository sportsRepository;
//
//     @Mock
//     private TicketInfoRepository ticketInfoRepository;
//
//     @Mock
//     private StadiumRepository stadiumRepository;
//
//     @InjectMocks
//     TicketInfoService ticketInfoService;
//
//     static TicketInfoRequestDto requestDto;
//
//     @BeforeAll
//     static void beforeAll() {
//
//         LocalDateTime openDateTime = LocalDateTime.of(2023, 12, 8, 12, 0); // 원하는 날짜와 시간으로 설정
//         LocalDateTime closeDateTime = LocalDateTime.of(2023, 12, 10, 18, 0); // 원하는 날짜와 시간으로 설정
//
//         requestDto = TicketInfoRequestDto.builder()
//                 .sportsId(1L)
//                 .openDate(openDateTime)
//                 .closeDate(closeDateTime)
//                 .ticketPrice(10000L)
//                 .stock(100L)
//                 .build();
//     }
//
//
//     @Test
//     @DisplayName("티켓정보 생성")
//     void test1() {
//         // given
//         Stadium stadium = new Stadium();
//         Sports sports = Sports.builder().sportId(1L).stadium(stadium).build();
//         given(sportsRepository.findById(requestDto.getSportsId())).willReturn(Optional.of(sports));
//
//         // when
//         TicketInfoService ticketInfoService = new TicketInfoService(ticketInfoRepository, sportsRepository);
//         TicketInfoResponseDto result = ticketInfoService.createTicketInfo(requestDto);
//
//         // then
//         System.out.println(result.getOpenDate());
//         assertEquals(requestDto.getOpenDate(), result.getOpenDate());
//     }
//
//     @Test
//     @DisplayName("티켓 정보 조회")
//     void test2() {
//         // given
//         Long ticketInfoId = 1L;
//
//         // when-then
//         TicketInfoService ticketInfoService = new TicketInfoService(ticketInfoRepository, sportsRepository);
//         Exception exception = assertThrows(TicketInfoNotFoundException.class, () ->
//                 ticketInfoService.getTicketInfo(ticketInfoId));
//
//         assertEquals("TicketInfo를 찾을 수 없습니다.", exception.getMessage());
//     }
//
//     @Test
//     @DisplayName("티켓 정보 수정")
//     void test3() {
//         // given
//         Long ticketInfoId = 1L;
//         Stadium stadium = Stadium.builder().stadiumId(1L).stadiumName("고척돔").build();
//         Sports sports = Sports.builder().sportId(1L).stadium(stadium).build();
//         TicketInfo existingTicketInfo = TicketInfo.builder()
//                 .ticketInfoId(1L)
//                 .sports(sports)
//                 .ticketPrice(10L).build();
//
//         given(ticketInfoRepository.findById(ticketInfoId)).willReturn(Optional.of(existingTicketInfo));
//
//         // when
//         TicketInfoResponseDto responseDto = ticketInfoService.updateTicketInfo(ticketInfoId, requestDto);
//
//         // Then
//         verify(ticketInfoRepository, times(1)).findById(ticketInfoId);
//         assertEquals(responseDto.getId(), ticketInfoId);
//     }
//
//     @Test
//     @DisplayName("티켓 정보 삭제")
//     void test4() {
//         // given
//         Long ticketInfoId = 1L;
//         TicketInfo ticketInfo = new TicketInfo();
//         given(ticketInfoRepository.findById(ticketInfoId)).willReturn(Optional.of(ticketInfo));
//
//         // when
//         String result = ticketInfoService.deleteTicketInfo(1L);
//
//         // then
//         assertEquals(result, "티켓 정보를 삭제하였습니다.");
//     }
// }
