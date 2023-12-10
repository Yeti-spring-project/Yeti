package com.example.yetiproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.yetiproject.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
	@Query(value="select * from tickets where user_id = ? ", nativeQuery = true)
	List<Ticket> findUserTicketList(Long userId);

	@Query(value="select * from tickets where user_id = ? and ticket_id = ? ", nativeQuery = true)
	Optional<Ticket> findUserTicket(Long userId, Long ticketId);
}
