package com.example.yetiproject.kafka.service;

import org.springframework.stereotype.Service;

import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.kafka.producer.TicketStockProducer;
import com.example.yetiproject.kafka.repository.StockCountRepository;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "kafka_reserve")
@RequiredArgsConstructor
public class TicketReserveService {
	private final TicketInfoRepository ticketInfoRepository;
	private final TicketStockProducer ticketStockProducer;
	private final StockCountRepository stockCountRepository; //redis incr 사용

	public void reserve(Long userId, Long ticketInfoId){
		// stock 수 count
		Long count = stockCountRepository.increment();
		System.out.println("count " + count);
		TicketInfo ticketInfo = ticketInfoRepository.findById(ticketInfoId).get();
		if( count > ticketInfo.getStock()){
			// stock 수 보다 많으면 return
			System.out.println("test");
			return;
		}
		ticketStockProducer.stockCreate(userId);
	}
}
