package com.example.yetiproject.facade;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.service.TicketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j(topic = "Kafka-Redission")
@Service
public class TicketReserveRedissonService {
	private final RedissonClient redissonClient;
	private final TicketService ticketService;

	public void reserveTicket(User user, TicketRequestDto ticketRequestDto){
		RLock lock = redissonClient.getLock("ticket- " + ticketRequestDto.getUserId());
		try{
			boolean isLocked = lock.tryLock(3, 3, TimeUnit.SECONDS);
			if(!isLocked){
				throw new LockedException("점유로 인한 예매 불가");
			}
			ticketService.reserveTicket(user, ticketRequestDto);
		}catch (InterruptedException e){
			log.error(e.getMessage());
		}finally {
			lock.unlock();
		}
	}


}
