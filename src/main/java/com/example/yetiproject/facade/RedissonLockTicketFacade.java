package com.example.yetiproject.facade;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j(topic = "redisson")
@RequiredArgsConstructor
public class RedissonLockTicketFacade {

    private final RedissonClient redissonClient;
    private final TicketService ticketService;

    // 예매하기 부분에 Redisson으로 락을 걸어줌
    public TicketResponseDto reserveTicket(User user, TicketRequestDto requestDto) {
        Long ticketInfoId = requestDto.getTicketInfoId();
        RLock lock = redissonClient.getLock(ticketInfoId.toString());
        TicketResponseDto responseDto;
        log.info("lock 획득 시도");
        try {
            // lock 획득 시도 시간, lock 점유 시간
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

            if (!available) {
                log.info("lock 획득 실패");
                return new TicketResponseDto();
            }
            log.info("lock 획득 성공");
            log.info("( posX, posY ) = " + requestDto.getSeat());
            responseDto = ticketService.reserveTicket(user, requestDto);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return responseDto;
    }
}
