package com.example.yetiproject.facade;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.dto.ticket.TicketResponseDto;
import com.example.yetiproject.entity.Seat;
import com.example.yetiproject.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j(topic = "예매하기")
@RequiredArgsConstructor
public class RedissonLockTicketFacade {

    private final RedissonClient redissonClient;
    private final TicketService ticketService;

    // 예매하기 부분에 Redisson으로 락을 걸어줌
    public TicketResponseDto reserveTicket(UserDetailsImpl userDetails, TicketRequestDto requestDto) {
        //Long ticketInfoId = requestDto.getTicketInfoId();
        Seat seat = new Seat(requestDto.getPosX(), requestDto.getPosY());
        RLock lock = redissonClient.getLock("ticket seat- " + seat);
        TicketResponseDto responseDto;

        try {
            // lock 획득 시도 시간, lock 점유 시간
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                log.info("lock 획득 실패");
                return new TicketResponseDto();
            }
            responseDto = ticketService.reserveTicket(userDetails.getUser(), requestDto);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //lock.unlock();
            unlock("ticket seat- " + seat , lock);
        }

        return responseDto;
    }

    /**
     * 기존에 leaseTimeoutMills 보다 작업이 오래될 경우 LOCK 이 자동적으로 풀려 IllegalMonitorException 이 발생할 수 있다.
     */
    private void unlock(String lockName, RLock lock){
        try{
            lock.unlock();
            log.debug("[DistributedLockProvider][execute] {} 정상적으로 LOCK 해제", lockName);
        } catch(IllegalMonitorStateException e){
            log.error("[DistributedLockProvider][execute] {} 이미 해제된 Lock 입니다.", lockName);
        } catch (Exception e){
            log.error("[DistributedLockProvider][execute] {} LOCK 해제시 문제 발생", lockName, e);
        }
    }
}
