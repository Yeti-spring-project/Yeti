package com.example.yetiproject.repository;

import java.util.List;
import java.util.Optional;

import com.example.yetiproject.entity.Ticket;
import com.example.yetiproject.entity.TicketInfo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketInfoRepository extends JpaRepository<TicketInfo, Long> {
	@Override //기본 적으로 findById 을 제공하기 때문에 Override 하여 재정의 후 사용
	//@EntityGraph(attributePaths = {"sports","sports.stadium"})
	Optional<TicketInfo> findById(Long ticketId);

	@Query(value="select stock from ticket_info where ticket_info_id = ?", nativeQuery = true)
	Long getStockforTicketInfo(Long ticketInfoId);

}
